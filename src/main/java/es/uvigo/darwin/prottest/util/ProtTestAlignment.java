package es.uvigo.darwin.prottest.util;

import java.io.PrintWriter;

import pal.alignment.Alignment;
import es.uvigo.darwin.prottest.global.ApplicationGlobals;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;

/**
 * The Class ProtTestAlignment provides some operations about
 * Alignment, as could be the empirical frequencies or the
 * sample size calculation.
 *
 * @author Francisco Abascal
 * @author Diego Darriba
 * @since 3.0
 */
public abstract class ProtTestAlignment {

	/**
	 * Calculate sample size.
	 * 
	 * @param alignment the alignment to calculate sample size
	 * @param sampleSizeMode the sample size mode
	 * @param customSampleSize the user sample size
	 * 
	 * @return the double
	 */
	public static double calculateSampleSize(Alignment alignment, int sampleSizeMode, double customSampleSize) {
		//For AICc and  BIC frameworks
		double sampleSize = 0.0;

		if(sampleSizeMode == ApplicationGlobals.SIZEMODE_USERSIZE) {
			sampleSize = customSampleSize;
		} else if(sampleSizeMode == ApplicationGlobals.SIZEMODE_ALIGNMENT) {
			sampleSize = (double)alignment.getSiteCount();
		} else if(sampleSizeMode == ApplicationGlobals.SIZEMODE_ALIGNMENT_VAR) {
			sampleSize = (double)alignment.getSiteCount() - calculateInvariableSites(alignment, false);
		} else if(sampleSizeMode == ApplicationGlobals.SIZEMODE_SHANNON || sampleSizeMode == ApplicationGlobals.SIZEMODE_SHANNON_NxL) {
			sampleSize = calculateShannonSampleSize(alignment, sampleSizeMode, false);
		} else if(sampleSizeMode == ApplicationGlobals.SIZEMODE_NxL) {
			sampleSize = (double)alignment.getSiteCount() * (double)alignment.getSequenceCount();
		}
		return sampleSize;
	}
	
	/**
	 * Calculate invariable sites.
	 * 
	 * @param alignment the alignment
	 * @param verbose the verbose
	 * 
	 * @return the int
	 */
	public static int calculateInvariableSites (Alignment alignment, boolean verbose) {
		//use this function to estimate a good starting value for the InvariableSites distribution.
		int numSites    = alignment.getSiteCount();
		int numSeqs     = alignment.getSequenceCount();
		int inv         = 0;
		int tmp;
		boolean tmpInv = true;
		for(int i=0; i < numSites; i++) {
			tmp     = indexOfChar(alignment.getData(0,i));
			tmpInv  = true;
			for(int j=0; j < numSeqs; j++) {
				if(indexOfChar(alignment.getData(j,i)) != tmp) { //if at least one difference in column i:
					j = numSeqs;    //we exit this for.
					tmpInv = false; //i is not an invariable site.
				}
			}
			if(tmpInv)
				inv++;
		}
		if(verbose)
			System.out.println("Observed number of invariant sites: " + inv);
		return inv;
	}
	
	/**
	 * Calculate shannon sample size.
	 * 
	 * @param alignment the alignment
	 * @param sampleSizeMode the sample size mode
	 * @param verbose the verbose
	 * 
	 * @return the double
	 */
	public static double calculateShannonSampleSize (Alignment alignment, int sampleSizeMode, boolean verbose) {

		//TODO: effect of gaps.
		//TODO: hacer gráfica que representa, según aceptamos más  divergencia en un alineamiento (y más
		//          secuencias, claro) cómo va cambiando la ShannonSampleSize. P.e. con RAS
		//SitePattern sP = SitePattern.getSitePattern(alignment);

		int    numSites       = alignment.getSiteCount();
		int    numSeqs        = alignment.getSequenceCount();
		//int    pattern[][]    = new int   [numSites][numSeqs];
		double freqs  [][]    = new double[numSites][ApplicationGlobals.NUM_STATES];
		byte   state  [][]    = new byte  [numSites][ApplicationGlobals.NUM_STATES];
		double siteS  []      = new double[numSites];
		int    sequences[]    = new int[numSites];
		double shannonEntropy = 0.0;

		//We simply count aminoacids at positions and store in state[][]
		if(verbose)
			System.err.println("Num sites= "+numSites+"; Num seqs= "+numSeqs);
		for(int i=0; i < numSites; i++) {
			for(int j=0; j < numSeqs; j++) {
				//state[i][indexOfchar(alignment.getData(j,i))]++;
				int index = indexOfChar(alignment.getData(j,i));
				if(index >= 0) {
					state[i][index]++;
					sequences[i]++;
				}
				//state[i][sP.pattern[j][i]]++;
			}
		}

		//For each alignment position, we calculate aminoacid frequencies. And also...
		//For each alignment position, we calculate Shannon Entropy based on previous frequencies.
		for(int i=0; i < numSites; i++) {
			for(int j=0; j < ApplicationGlobals.NUM_STATES; j++) {
				//freqs[i][j] = (double)state[i][j]/(double)numSeqs;
				freqs[i][j] = (double)state[i][j]/(double)sequences[i];
				if(freqs[i][j] > 0)
					siteS[i]   += freqs[i][j]*Math.log(freqs[i][j])/Math.log(2);
				if(verbose) 
					System.out.println("[DEBUG] Math.log(freqs): "+Math.log(freqs[i][j]) +
							"  ///  freqs: "+i+"-"+j+": "+freqs[i][j] );
			}
			if(verbose)
				System.out.println("[DEBUG] siteS["+i+"]: "+siteS[i]);
		}

		//We sum positions entropies over the whole alignment.
		for(int i=0; i < numSites; i++) {
			shannonEntropy += siteS[i];
		}

		//Graphical Staff: the alignment and its variability.
		if(verbose) {
			for(int seq=0; seq < numSeqs; seq++)
				System.err.println(alignment.getAlignedSequenceString(seq));
			for(double y=-4.0; y<=0.0; y=y+0.5) {
				for(int site=0; site < numSites; site++) {
					if(siteS[site] <= y)
						System.err.print("|");
					else
						System.err.print(" ");
				}
				System.err.println("");
			}
		}

		if (verbose) {
			for(int i=0; i < numSites; i++) {
				System.out.print("[DEBUG] Position: " + i);
				for(int j=0; j < ApplicationGlobals.NUM_STATES; j++) {
					System.out.print("\t"+state[i][j]);
				}
				System.out.println("");
			}
		}
		
		if(sampleSizeMode == ApplicationGlobals.SIZEMODE_SHANNON) {
			return -1.0*shannonEntropy; //sum of shannon Entropy positions.
		} else { //if Options.SHANNON_NxL
			double meanShannonEntropy;
			double maxShannonEntropy = 0;
			double normalizedShannonEntropy = 0;
			meanShannonEntropy = -1.0*shannonEntropy/(double)numSites; //mean S for sites
			//let's normalize ShannonEntropy from 0 to 1:
			for(int i=0; i<ApplicationGlobals.NUM_STATES; i++) {
				maxShannonEntropy += (1.0/(double)ApplicationGlobals.NUM_STATES)*Math.log(1.0/(double)ApplicationGlobals.NUM_STATES)/Math.log(2);
			}
			//by this moment we normalize by a "regla de tres" (POR HACER: ver si hay otra alternativa mejor)
			//POR HACER: estudiar la distribución de las ShannonEntropies
			//POR HACER: quizás la corrección también haya que aplicarla al número de sitios: ¿normalizedShannonEntropy*normalizedShannonEntropy?
			normalizedShannonEntropy = -1.0*meanShannonEntropy/maxShannonEntropy;
			if(verbose)
				System.err.println("[DEBUG] Max S="+maxShannonEntropy+"; Mean S="+meanShannonEntropy+"; Normalized S="+normalizedShannonEntropy);
			return (double)numSites*(double)numSeqs*normalizedShannonEntropy; //NxL x averaged Shannon entropy
		}
	}
	
	/**
	 * Gets the frequencies.
	 * 
	 * @param alignment the alignment
	 * 
	 * @return the frequencies
	 */
	public static double[] getFrequencies (Alignment alignment) {
		int    numSites       = alignment.getSiteCount    ()   ;
		int    numSeqs        = alignment.getSequenceCount()   ;
		double freqs[]        = new double[ApplicationGlobals.NUM_STATES]          ;
		int    aas  []        = new int   [ApplicationGlobals.NUM_STATES]          ;
		int    total_aas      = 0                              ;
		for(int i=0; i < ApplicationGlobals.NUM_STATES; i++) aas[i] = 0;

		for(int i=0; i < numSites; i++)
			for(int j=0; j < numSeqs; j++) {
				int index = indexOfChar(alignment.getData(j,i));
				if(index >= 0) {
					aas[index]++;
					total_aas ++;
				}
			}

		for(int i=0; i < ApplicationGlobals.NUM_STATES; i++)
			freqs[i] = (double)aas[i]/(double)total_aas;

		return freqs;
	}
	
	/**
	 * Prints the frequencies.
	 * 
	 * @param freqs the freqs
	 * @param out the out
	 */
	public static void printFrequencies (double[] freqs, PrintWriter out) {
		
		out.println("Observed aminoacid frequencies:");
		for(int i=0; i<ApplicationGlobals.NUM_STATES; i++) {
			char aa = charOfIndex(i);
			out.print(" " + aa + ": " + ProtTestFormattedOutput.getDecimalString(freqs[i],3) + "   ");
			if( (i+1)%5 == 0 )
				out.println("");
		}
	}

	/**
	 * Char of index.
	 * 
	 * @param c the c
	 * 
	 * @return the char
	 */
	public static char charOfIndex (int c) {
		char[] charSet = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 
				'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y'};
		if (c >= 0 && c <= charSet.length)
			return charSet[c];
		else {
			return '?';
		}
	}
	
	/**
	 * Index of char.
	 * 
	 * @param c the c
	 * 
	 * @return the int
	 */
	private static int indexOfChar (char c) {
		char[] charSet = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 
				'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y'};
		for (int charIndex = 0; charIndex < charSet.length; charIndex++)
			if (charSet[charIndex] == c)
				return charIndex;

		return -1;
	}
}