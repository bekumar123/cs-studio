/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;


/** Helper for merging archived samples.
 *  <p>
 *  New data is to some extend 'merged' with existing data:
 *  Where the time ranges overlap, the data replaces the old data.
 *
 *  @author Kay Kasemir
 */
public class PlotSampleMerger
{
    
     public static PlotSample[] merge(final PlotSample[] old, final PlotSample[] add) {
         final PlotSample[] A = old; 
         final PlotSample[] B = add;
         
     
         if (A == null) {
             return B;
         }
         if (B == null) {
             return A;
         }
         long start=B[0].getTime().getSec();
         long end=B[0].getTime().getSec();
         for (int aa = 0; aa < B.length; aa++) {
             if(B[aa].getTime().getSec()<start)  start=B[aa].getTime().getSec();
             if(B[aa].getTime().getSec()>end)  end=B[aa].getTime().getSec();
         }
         PlotSample C[] = new PlotSample[A.length + B.length];
         
         int a = 0;
         int b = 0;
         int c = 0;
         while (a < A.length) {
             if (A[a].getTime().getSec()<start) { 
                 C[c] = A[a];
                 c++;
             }
             a++;
          
         }
         while (b < B.length) {
             C[c] = B[b];
             b++;          
             c++;
         }
         a=0;
         while (a < A.length) {
             if (A[a].getTime().getSec()>end) { 
                 C[c] = A[a];
                 c++;
             }
             a++;
          
         }
     
         PlotSample result[] = new PlotSample[c];
         System.arraycopy(C, 0, result, 0, c);
         return result;
     }
}
