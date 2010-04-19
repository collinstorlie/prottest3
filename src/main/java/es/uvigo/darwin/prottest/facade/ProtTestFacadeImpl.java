package es.uvigo.darwin.prottest.facade;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.alignment.AlignmentParseException;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.exe.RunEstimator;
import es.uvigo.darwin.prottest.exe.util.TemporaryFileManager;
import es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO;
import es.uvigo.darwin.prottest.facade.util.SelectionChunk;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.observer.ObservableModelUpdater;
import es.uvigo.darwin.prottest.selection.AIC;
import es.uvigo.darwin.prottest.selection.AICc;
import es.uvigo.darwin.prottest.selection.BIC;
import es.uvigo.darwin.prottest.selection.DT;
import es.uvigo.darwin.prottest.selection.InformationCriterion;
import es.uvigo.darwin.prottest.selection.LNL;
import es.uvigo.darwin.prottest.selection.printer.AminoAcidPrintFramework;
import es.uvigo.darwin.prottest.selection.printer.PrintFramework;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.collection.ModelCollection;
import es.uvigo.darwin.prottest.util.collection.SingleModelCollection;
import es.uvigo.darwin.prottest.util.exception.AlignmentFormatException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.exception.TreeFormatException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.fileio.AlignmentReader;

// TODO: Auto-generated Javadoc
/**
 * An abstract implementation of the ProtTest facade.
 */
public abstract class ProtTestFacadeImpl
	extends ObservableModelUpdater
	implements ProtTestFacade {
	
//	/** The options. */
//	protected ApplicationOptions options;

	/** The Constant AIC. */
	public static final int AIC = SelectionChunk.AIC;
	
	/** The Constant BIC. */
	public static final int BIC = SelectionChunk.BIC;
	
	/** The Constant AICC. */
	public static final int AICC = SelectionChunk.AICC;
	
	/** The Constant DT. */
	public static final int DT = SelectionChunk.DT;
	
	/** The Constant LK. */
	public static final int LNL = SelectionChunk.LNL;
	
	/**
	 * Instantiates a new prot test facade implementation.
	 */
	public ProtTestFacadeImpl() {
	}
	
	
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#printModelsSorted(es.uvigo.darwin.prottest.selection.InformationCriterion, java.io.PrintWriter)
	 */
	public void printModelsSorted(InformationCriterion informationCriterion,
			PrintWriter outputWriter) {
		
		PrintFramework printFramework = new AminoAcidPrintFramework(informationCriterion);
		
		printFramework.printModelsSorted(outputWriter);
		
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#readAlignment(java.io.PrintWriter, java.lang.String, boolean)
	 */
	public Alignment readAlignment (PrintWriter out, String filename, boolean debug) 
		throws 	AlignmentFormatException {
		AlignmentReader reader = new AlignmentReader();
			
		return reader.readAlignment(out, filename, debug);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#readTree(java.io.PrintWriter, java.lang.String, boolean)
	 */
	public Tree readTree (PrintWriter out, String filename, boolean debug) 
	throws 	TreeFormatException {
		AlignmentReader reader = new AlignmentReader();
		
		return reader.readTree(out, filename, debug);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#update(es.uvigo.darwin.prottest.observer.Observable, java.lang.Object)
	 */
	public void update(ObservableModelUpdater o, Model model, ApplicationOptions options) {
		notifyObservers(model, options);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#computeInformationCriterion(pal.alignment.Alignment, es.uvigo.darwin.prottest.model.Model[], int, int, double)
	 */
	public SelectionChunk computeInformationCriterion(Alignment alignment, Model[] models, 
			int criterion, int sampleSizeMode, double sampleSize,
			double confidenceInterval) {
		
		ModelCollection modelCollection = new SingleModelCollection(models, alignment);
		
		InformationCriterion informationCriterion;

		double calculatedSampleSize = ProtTestAlignment.calculateSampleSize(alignment, sampleSizeMode, sampleSize);
		
		switch(criterion) {
		case AIC:
			informationCriterion = new AIC(modelCollection, confidenceInterval, calculatedSampleSize);
			break;
		case BIC:
			informationCriterion = new BIC(modelCollection, confidenceInterval, calculatedSampleSize);
			break;
		case AICC:
			informationCriterion = new AICc(modelCollection, confidenceInterval, calculatedSampleSize);
			break;
		case DT:
			informationCriterion = new DT(modelCollection, confidenceInterval, calculatedSampleSize);
			break;
		case LNL:
			informationCriterion = new LNL(modelCollection, confidenceInterval, calculatedSampleSize);
			break;
		default:
			throw new ProtTestInternalException("Unexistent information criterion");
		}
		
		return new SelectionChunk(informationCriterion);
		
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#getNumberOfThreads()
	 */
	public int getNumberOfThreads() {
		// single thread (default)
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#setNumberOfThreads(int)
	 */
	public void setNumberOfThreads(int numThreads) {
		throw new ProtTestInternalException("No threading support");
	}
	
	protected Tree calculateBionjJTT(ApplicationOptions options) {
	    
	    Model jttModel = ProtTestFactory.getInstance()
        .createModel("JTT", options.getDistribution("Uniform"), new Properties(), 
                options.getAlignment(), null, 0);
        TemporaryFileManager.synchronize( 
                options.getAlignment(), null, 1);
        RunEstimator treeEstimator
               = ProtTestFactory.getInstance()
               .createRunEstimator(options, jttModel);
        treeEstimator.run();
        
        return jttModel.getTree();
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#startAnalysis(es.uvigo.darwin.prottest.util.printer.ProtTestPrinter)
	 */
	public Model[] startAnalysis(ApplicationOptions options) {
		
		if(options.getTreeFile() == null) {
			// this way, the starting topology is the same for every model
			if (options.strategyMode == ApplicationGlobals.OPTIMIZE_FIXED_BIONJ) {
				// use JTT BIONJ Tree
				Tree jttTree = calculateBionjJTT(options);
				
				options.setTree(jttTree);
			}
		}
		TemporaryFileManager.synchronize(options.getAlignment(), options.getTree(), 
				getNumberOfThreads());
		Model[] models = analyze(options);
		
		return models;
	}
	
	/**
	 * Analyze.
	 * 
	 * @param options the execution options
	 * 
	 * @return the set of optimized models
	 */
	public abstract Model[] analyze(ApplicationOptions options);
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#configure(es.uvigo.darwin.prottest.facade.util.ProtTestParameterVO)
	 */
	public ApplicationOptions configure(ProtTestParameterVO parameters) 
		throws IOException, AlignmentParseException, ProtTestInternalException {
		ApplicationOptions options = new ApplicationOptions();
		options.setPrinter(parameters.getPrinter());
		options.setAlignFile(parameters.getAlignmentFilePath());
		options.setNumberOfCategories(parameters.ncat);
		for (String matrix : parameters.getMatrices()) {
			options.addMatrix(matrix);
		}
		for (String distribution : parameters.getDistributions()) {
			options.addDistribution(distribution);
		}
		options.setPlusF(parameters.isPlusF());
		options.setTreeFile(parameters.getTreeFilePath());
		options.setStrategyMode(parameters.getStrategyMode());
		return options;
	}
	
//	/* (non-Javadoc)
//	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#configure(es.uvigo.darwin.prottest.global.options.ApplicationOptions)
//	 */
//	public void configure(ApplicationOptions options) {
//		this.options = options;
//	}
	
//	/* (non-Javadoc)
//	 * @see es.uvigo.darwin.prottest.facade.ProtTestFacade#reportOptions(java.io.PrintWriter)
//	 */
//	public void reportOptions(PrintWriter out) {
//		ApplicationOptions.getInstance().report(out);
//	}

}