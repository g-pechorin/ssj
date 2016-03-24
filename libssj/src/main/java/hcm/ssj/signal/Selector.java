/*
 * Selector.java
 * Copyright (c) 2016
 * Authors: Ionut Damian, Michael Dietz, Frank Gaibler, Daniel Langerenken
 * *****************************************************
 * This file is part of the Social Signal Interpretation for Java (SSJ) framework
 * developed at the Lab for Human Centered Multimedia of the University of Augsburg.
 *
 * SSJ has been inspired by the SSI (http://openssi.net) framework. SSJ is not a
 * one-to-one port of SSI to Java, it is an approximation. Nor does SSJ pretend
 * to offer SSI's comprehensive functionality and performance (this is java after all).
 * Nevertheless, SSJ borrows a lot of programming patterns from SSI.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this library; if not, see <http://www.gnu.org/licenses/>.
 */

package hcm.ssj.signal;

import hcm.ssj.core.Cons;
import hcm.ssj.core.Log;
import hcm.ssj.core.Transformer;
import hcm.ssj.core.Util;
import hcm.ssj.core.stream.Stream;

/**
 * Selects specific values from a stream.<br>
 * Created by Frank Gaibler on 24.11.2015.
 */
public class Selector extends Transformer
{
    /**
     * All options for the transformer
     */
    public class Options
    {
        /**
         * Describes the output names for every dimension in e.g. a graph.
         */
        public String[] outputClass = null;
        /**
         * The output type for which the input stream has to match.
         */
        public Cons.Type outputType = Cons.Type.FLOAT;
        /**
         * The values to select.<br>
         * The ordering has to follow the order in the <class>Serializer</class>.<br>
         * The selection interval is given by the selection size.
         */
        public int[] values = {0};
        /**
         * The size of all values in one sample.
         */
        public int selectionSize = values.length;
    }

    public Options options = new Options();

    /**
     *
     */
    public Selector()
    {
        _name = "SSJ_transformer_" + this.getClass().getSimpleName();
    }

    /**
     * @param stream_in  Stream[]
     * @param stream_out Stream
     */
    @Override
    public void enter(Stream[] stream_in, Stream stream_out)
    {
        //check for valid stream
        if (stream_in.length != 1 || stream_in[0].dim < 1)
        {
            Log.e("invalid input stream");
            return;
        }
        //not all types are supported
        if (options.outputType == Cons.Type.CUSTOM || options.outputType == Cons.Type.UNDEF)
        {
            Log.e("output type is not supported");
        }
        //the streams should have the same type
        if (options.outputType != stream_in[0].type)
        {
            Log.e("invalid stream type");
        }
    }

    /**
     * @param stream_in  Stream[]
     * @param stream_out Stream
     */
    @Override
    public void transform(Stream[] stream_in, Stream stream_out)
    {
        switch (options.outputType)
        {
            case BOOL:
            {
                boolean[] out = stream_out.ptrBool();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrBool()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case BYTE:
            {
                byte[] out = stream_out.ptrB();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrB()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case CHAR:
            {
                char[] out = stream_out.ptrC();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrC()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case DOUBLE:
            {
                double[] out = stream_out.ptrD();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrD()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case FLOAT:
            {
                float[] out = stream_out.ptrF();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrF()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case INT:
            {
                int[] out = stream_out.ptrI();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrI()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case LONG:
            {
                long[] out = stream_out.ptrL();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrL()[value + options.selectionSize * i];
                    }
                }
                break;
            }
            case SHORT:
            {
                short[] out = stream_out.ptrS();
                for (int i = 0, z = 0; i < stream_in[0].num; i++)
                {
                    for (int value : options.values)
                    {
                        out[z++] = stream_in[0].ptrS()[value + options.selectionSize * i];
                    }
                }
                break;
            }
        }
    }

    /**
     * @param stream_in Stream[]
     * @return int
     */
    @Override
    public int getSampleDimension(Stream[] stream_in)
    {
        return options.values.length;
    }

    /**
     * @param stream_in Stream[]
     * @return int
     */
    @Override
    public int getSampleBytes(Stream[] stream_in)
    {
        return Util.sizeOf(options.outputType);
    }

    /**
     * @param stream_in Stream[]
     * @return Cons.Type
     */
    @Override
    public Cons.Type getSampleType(Stream[] stream_in)
    {
        return options.outputType;
    }

    /**
     * @param sampleNumber_in int
     * @return int
     */
    @Override
    public int getSampleNumber(int sampleNumber_in)
    {
        return sampleNumber_in;
    }

    /**
     * @param stream_in  Stream[]
     * @param stream_out Stream
     */
    @Override
    protected void defineOutputClasses(Stream[] stream_in, Stream stream_out)
    {
        int overallDimension = getSampleDimension(stream_in);
        stream_out.dataclass = new String[overallDimension];
        if (options.outputClass != null)
        {
            if (overallDimension == options.outputClass.length)
            {
                System.arraycopy(options.outputClass, 0, stream_out.dataclass, 0, options.outputClass.length);
                return;
            } else
            {
                Log.w("invalid option outputClass length");
            }
        }
        for (int i = 0; i < stream_out.dataclass.length; i++)
        {
            stream_out.dataclass[i] = "slctr" + i;
        }
    }
}