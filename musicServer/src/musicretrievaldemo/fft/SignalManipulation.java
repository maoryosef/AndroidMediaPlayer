/* Derek Hoiem
 * August, 2004
 */

package musicretrievaldemo.fft;

public class SignalManipulation {

	public static Complex[] doubleToComplex(double[] x) {
		Complex[] y = new Complex[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = new Complex(x[i], 0.0);
		}
		return y;
	}

	public static double[] complexToDouble(Complex[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = x[i].re;
		}
		return y;
	}

	private static int getFFTIndexOfFrequency(int fftLength, int fs, double freq) {
		double numSec = fftLength / (double)fs;
		int index = (int)(freq*numSec);
                return index;
	}


	public static double[] removeFrequencies(double[] x, int fs, 
                                                 double[] firstFreqToRemove, 
                                                 double[] lastFreqToRemove) {
	
		Complex[] fftX = FFT.fft(doubleToComplex(x));
		int fftLength = x.length;
		int fftHalfLength = x.length/2;

		for (int i = 0; i < firstFreqToRemove.length; i++) {
			int index1 = getFFTIndexOfFrequency(fftLength, fs, firstFreqToRemove[i]);
			int index2 = getFFTIndexOfFrequency(fftLength, fs, lastFreqToRemove[i]);
			for (int k = index1; k <= index2; k++) {
				fftX[k].re = 0.0;
				fftX[k].im = 0.0;
				fftX[k+fftHalfLength].re = 0.0;
				fftX[k+fftHalfLength].im = 0.0;
			}
		}

		Complex[] y = FFT.ifft(fftX);
		return complexToDouble(y);
	}


	// returns y[band][time] where y records the power of the windowed fft
	public static double[][] performWindowedFFT(double[] x, int fs, double[] freqs, int windowSize, int step) {

		int numWindows = (x.length-windowSize)/step;
		double[][] y = new double[freqs.length-1][numWindows];
		double[] windowX = new double[windowSize];
		for (int w = 0; w < numWindows; w++) {
			int firstX = w*step;
			int lastX = firstX + windowSize-1;
		
			for (int i = firstX; i <=lastX; i++) {
				windowX[i-firstX] = x[i];
			}

			Complex[] fftX = FFT.fft(doubleToComplex(windowX));
				
			for (int b = 0; b < freqs.length - 1; b++) {
				int first = getFFTIndexOfFrequency(windowSize, fs, freqs[b]);
                            int last = getFFTIndexOfFrequency(windowSize, fs, freqs[b+1]);                                
				
                                y[b][w] = 0.0;
				for (int i=first; i<last; i++) {
					Complex c = fftX[i];
					y[b][w] += c.re*c.re + c.im*c.im;
 				}
			}
		}
                return y;
	}
}

