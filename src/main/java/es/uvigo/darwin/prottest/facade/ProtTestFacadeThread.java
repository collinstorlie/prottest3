/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.facade;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.Utilities;
import es.uvigo.darwin.prottest.util.checkpoint.CheckPointManager;
import es.uvigo.darwin.prottest.util.checkpoint.status.ProtTestStatus;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.comparator.AminoAcidModelNaturalComparator;
import es.uvigo.darwin.prottest.util.comparator.ModelDistributionHeuristic;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * A parallel implementation of the ProtTest facade.
 */
public class ProtTestFacadeThread
        extends ProtTestFacadeImpl {

    private CheckPointManager cpManager;
    private ModelCollection modelSet;
    /** The thread pool size. */
    private int poolSize;
    /** The ProtTest factory to instantiate some application objects. */
    private ProtTestFactory factory = ProtTestFactory.getInstance();
    private ExecutorService threadPool;

    /**
     * Instantiates a new parallel ProtTest facade.
     * 
     * @param poolSize the number of parallel processes
     */
    public ProtTestFacadeThread(int poolSize) {
        this.poolSize = poolSize;
//		options.setNumberOfThreads(poolSize);
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#getNumberOfThreads()
     */
    @Override
    public int getNumberOfThreads() {
        return poolSize;
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#setNumberOfThreads(int)
     */
    @Override
    public void setNumberOfThreads(int numThreads) {
//		options.setNumberOfThreads(numThreads);
        this.poolSize = numThreads;
    }

    public Model[] analyze(ApplicationOptions options) {

        this.threadPool = Executors.newFixedThreadPool(this.poolSize);

        //For each model, for each distribution,... optimize the model and calculate some statistics:

        println("**********************************************************");
        //this is only for doing output prettier
        println("Observed number of invariant sites: " + ProtTestAlignment.calculateInvariableSites(options.getAlignment(), false));
        StringWriter sw = new StringWriter();
        ProtTestAlignment.printFrequencies(ProtTestAlignment.getFrequencies(options.getAlignment()), new PrintWriter(sw));
        sw.flush();
        println(sw.toString());
        println("**********************************************************");
        println("");

        //TimeStamp timer = new TimeStamp();
        Date startDate = new Date();
        long startTime = startDate.getTime();

        int numberOfModels = 0;

        ModelCollection arrayListModel;
        //Adding support for checkpointing
        ProtTestStatus initialStatus = new ProtTestStatus(null, options);

        cpManager = new CheckPointManager();
        if (cpManager.loadStatus(initialStatus)) {
            arrayListModel = new SingleModelCollection(
                    ((ProtTestStatus) cpManager.getLastCheckpoint()).getModels(),
                    options.getAlignment());
        } else {
            arrayListModel = new SingleModelCollection(options.getAlignment());
            Properties modelProperties = new Properties();
            if (options.isPlusF()) {
                modelProperties.setProperty(Model.PROP_PLUS_F, "true");
            }
            arrayListModel.addModelCartesian(options.getMatrices(), options.getDistributions(), modelProperties,
                    options.getAlignment(), options.getTree(), options.ncat);
        }

        ModelWeightComparator comparator = new ModelDistributionHeuristic();
        Collections.sort(arrayListModel, comparator);

        numberOfModels = arrayListModel.size();
        modelSet = arrayListModel;

        flush();

        RunEstimator[] runenv = new RunEstimator[modelSet.size()];
        Collection<Callable<Object>> c = new ArrayList<Callable<Object>>();

        int current = 0;
        for (Model model : modelSet) {

            runenv[current] = factory.createRunEstimator(options, model);
            runenv[current].addObserver(this);

            c.add(Executors.callable(runenv[current]));
            current++;

        }

        boolean errorsFound = false;
        Collection<Future<Object>> futures = null;
        try {
            futures = threadPool.invokeAll(c);
        } catch (InterruptedException e) {
        }
        if (futures != null) {
            for (Future<Object> f : futures) {
                try {
                    f.get();
                                    } catch (InterruptedException ex) {
                    errorsFound = true;
                } catch (ExecutionException ex) {
                    // Internal exception while computing model.
                    // Let's continue with errors
                    errorsFound = true;
                }
            }
        }

        long endTime = System.currentTimeMillis();

        //check all models and remove those with errors
        for (current = 0; current < runenv.length; current++) {
            Model model = runenv[current].getModel();
            if (!model.isComputed()) {
                arrayListModel.remove(model);
                numberOfModels--;
                warnln("There were errors computing model " + model.getModelName());
            }
        }

        println("");
        println("");

        // print optimization reports sorted
        Collections.sort(arrayListModel, new AminoAcidModelNaturalComparator());
        for (Model model : arrayListModel) {
            for (current = 0; current < runenv.length; current++) {
                if (runenv[current].getModel().equals(model)) {
                    runenv[current].report();
                    break;
                }
            }
        }
        flush();

        Model[] allModels = new Model[numberOfModels];

        println("************************************************************");
        String runtimeStr = Utilities.calculateRuntime(startTime, endTime);
        println("Date   :  " + (new Date()).toString());
        println("Runtime:  " + runtimeStr);
        println("");
        println("");
        allModels = arrayListModel.toArray(new Model[0]);

        cpManager.done();
        return allModels;
    }

//	public ApplicationOptions configure(ProtTestParameterVO parameters) 
//	throws IOException, AlignmentParseException, ProtTestInternalException {
//		ApplicationOptions.synchronize();
////		options.setNumberOfThreads(poolSize);
//		return super.configure(parameters);
//	}
    @Override
    public synchronized void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {

        if (model.isComputed() && options != null) {
            if (cpManager != null) {
                ProtTestStatus newStatus = new ProtTestStatus(modelSet.toArray(new Model[0]), options);
                cpManager.setStatus(newStatus);
            }
        }

        super.update(o, model, options);
    }
}
