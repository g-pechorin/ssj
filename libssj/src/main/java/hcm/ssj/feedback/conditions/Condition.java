/*
 * Behaviour.java
 * Copyright (c) 2015
 * Author: Ionut Damian
 * *****************************************************
 * This file is part of the Logue project developed at the Lab for Human Centered Multimedia
 * of the University of Augsburg.
 *
 * The applications and libraries are free software; you can redistribute them and/or modify them
 * under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or any later version.
 *
 * The software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package hcm.ssj.feedback.conditions;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import hcm.ssj.core.Log;
import hcm.ssj.core.event.Event;


/**
 * Created by Johnny on 01.12.2014.
 */
public class Condition
{
    protected String _event;
    protected String _sender;

    public float thres_lower = 0;
    public float thres_upper = 0;

    public static Condition create(XmlPullParser xml, Context context)
    {
        Condition b = null;
        if (xml.getAttributeValue(null, "type").equalsIgnoreCase("SpeechRate"))
            b = new SpeechRate();
        else if (xml.getAttributeValue(null, "type").equalsIgnoreCase("Loudness"))
            b = new Loudness();
        else if (xml.getAttributeValue(null, "type").equalsIgnoreCase("KeyPress"))
            b = new KeyPress();
        else
            b = new Condition();

        b.load(xml, context);
        return b;
    }

    public boolean checkEvent(Event event)
    {
        if (event.name.equalsIgnoreCase(_event)
        && event.sender.equalsIgnoreCase(_sender))
        {
            float value = parseEvent(event);
            if((value == thres_lower) || (value >= thres_lower && value < thres_upper))
                return true;
        }

        return false;
    }

    public float parseEvent(Event event)
    {
        switch(event.type)
        {
            case BYTE:
                return (float) event.ptrB()[0];
            case CHAR:
            case STRING:
                return Float.parseFloat(event.ptrStr());
            case SHORT:
                return (float) event.ptrShort()[0];
            case INT:
                return (float) event.ptrI()[0];
            case LONG:
                return (float) event.ptrL()[0];
            case FLOAT:
                return event.ptrF()[0];
            case DOUBLE:
                return (float) event.ptrD()[0];
            case BOOL:
                return (event.ptrBool()[0]) ? 1 : 0;
            default:
                throw new UnsupportedOperationException("unknown event");
        }
    }

    protected void load(XmlPullParser xml, Context context)
    {
        try
        {
            xml.require(XmlPullParser.START_TAG, null, "condition");

            _event = xml.getAttributeValue(null, "event");
            _sender = xml.getAttributeValue(null, "sender");

            String from = xml.getAttributeValue(null, "from");
            String equals = xml.getAttributeValue(null, "equals");
            String to = xml.getAttributeValue(null, "to");

            if(equals != null)
            {
                thres_lower = Float.parseFloat(equals);
            }
            else if(from != null && to != null)
            {
                thres_lower = Float.parseFloat(from);
                thres_upper = Float.parseFloat(to);
            }
            else
            {
                throw new IOException("threshold value(s) not set");
            }
        }
        catch(IOException | XmlPullParserException e)
        {
            Log.e("error parsing config file", e);
        }
    }
}
