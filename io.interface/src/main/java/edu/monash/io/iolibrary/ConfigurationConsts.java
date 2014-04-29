package edu.monash.io.iolibrary;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidIOTypeException;
/**
* @author Hoang nguyen
* This class contains constants related to configurations
*/
public class ConfigurationConsts{
	//common
	/*type*/
	public static final String TYPE = "type";
	/*type - container*/
	public static final String TYPE_CONTAINER = "container";
	/*type - channel*/
	public static final String TYPE_CHANNEL = "channel";

	/*type - channel - configuration - connection - socketio*/
	public static final String TYPE_SOCKETIO = "socketio";
	
	// /*type - input*/
	// public static final String TYPE_INPUT = "input";
	// /*type - output*/
	// public static final String TYPE_OUTPUT = "output";
	// /*type - inout*/
	// public static final String TYPE_INOUT = "inout";	
	// replaced bt IoType
	public static enum IoType {
        INPUT, OUTPUT, INOUT;
        public static IoType fromString(String s) throws InvalidIOTypeException{
            if (s != null) {
                IoType[] vs = values();
                for (int i = 0; i < vs.length; i++) {
                    if (vs[i].toString().equalsIgnoreCase(s)) {
                        return vs[i];
                    }
                }
            }
	        throw new InvalidIOTypeException("IOType:" + s + " is not supported");
        }
        public static String[] stringValues() {
            IoType[] vs = values();
            String[] ss = new String[vs.length];
            for (int i = 0; i < vs.length; i++) {
                ss[i] = vs[i].toString();
            }
            return ss;
        }
    }




	/*name*/
	public static final String NAME = "name";
	
	/*about*/
	public static final String ABOUT = "about";
	/*about - label*/
	public static final String ABOUT_LABEL = "label";
	/*about - description*/
	public static final String ABOUT_DESCRIPTION = "description";
	/*baout - hint*/
	public static final String ABOUT_HINT = "hint";


	//container
	/*author*/
	public static final String AUTHOR = "author";
	/*date*/
	public static final String DATE = "date";
	/*layout*/
	public static final String LAYOUT = "layout";
	/*shared io*/
	public static final String SHARED_IO = "shared";
	/*layout - vertical*/
	public static final String LAYOUT_VERTICAL = "vertical";
	/*layout - horixontal*/
	public static final String LAYOUT_HORIZONTAL = "horizontal";

	//io channel	
	/*configuration*/
	public static final String CONFIG = "configuration";
	/* config - connection - host*/
	public static final String CONNECTION_HOST = "host";
	/*config - connection - protocol*/
	public static final String CONNECTION_PROTOCOL = "protocol";	
	/* config - connection - port*/
	public static final String CONNECTION_PORT = "port";
	/* config - connection - username*/
	public static final String CONNECTION_USENAME = "username";
	/* config - connection - pass*/
	public static final String CONNECTION_PASS = "pass";
	/* config - connection - timout*/
	public static final String CONNECTION_TIMEOUT = "timeout";
	/* config - connection - nsp*/
	public static final String CONNECTION_NSP = "nsp";

	/* containerid*/
	public static final String CONTAINERID = "containerid";
	
	
	/*variable*/
	/*variable -data type*/
	public static final String IN_DATA_TYPE  = 	"in_datatype";
	public static final String OUT_DATA_TYPE = 	"out_datatype";

	/*variable - data*/
	public static final String IN_DATA = "in_data";
	public static final String OUT_DATA = "out_data";	
	/*variable - gui element*/
	public static final String GUI_ELEMENT = "guielement";


	/*channel - type - choice*/
	// public static final String TYPE_CHOICE = "choice";
	/*channel - type - choice - options*/
	public static final String CHOICE_OPTIONS = "options";

	/*channel -type - plot*/
	// public static final String TYPE_LINE_CHART = "line_chart";
 //    public static final String LINE_CHART_ASPECT_RATIO = "aspect_ratio";

    /*channel - data - update mode*/
    public static final String DATA_UPDATE_MODE = "update_mode";
	
	//data type
	// IMAGE data will be sent as base64 string, same as VIDEO
	public static enum DataType {
        INT, DOUBLE, BOOL, STRING, OBJECT, COMMA_SEPERATED_STRING, JSON_STRING, BYTEARRAY, 
        TEXT, BINARY, GENERAL, ARRAY_INT, ARRAY_DOUBLE, ARRAY_STRING, ARRAY_GENERAL, IMAGE, VIDEO;
        //TEXT and BINARY are two file types, BYTEARRAY, TEXT, BINARY are not yet supported on the actor side
        public static DataType fromString(String s) throws InvalidDataTypeException{
            if (s != null) {
                DataType[] vs = values();
                for (int i = 0; i < vs.length; i++) {
                    if (vs[i].toString().equalsIgnoreCase(s)) {
                        return vs[i];
                    }
                }
            }
            throw new InvalidDataTypeException("[ConfigurationConsts:Datatype]" + s + " is not supported");
        }
        public static String[] stringValues() {
            DataType[] vs = values();
            String[] ss = new String[vs.length];
            for (int i = 0; i < vs.length; i++) {
                ss[i] = vs[i].toString();
            }
            return ss;
        }
    }

    //update mode
    public static enum UpdateMode {
        APPEND, OVERWRITE;
        public static UpdateMode fromString(String s) throws InvalidDataTypeException{
            if (s != null) {
                UpdateMode[] vs = values();
                for (int i = 0; i < vs.length; i++) {
                    if (vs[i].toString().equalsIgnoreCase(s)) {
                        return vs[i];
                    }
                }
            }
            throw new InvalidDataTypeException("Datatype:" + s + " is not supported");
        }
        public static String[] stringValues() {
            UpdateMode[] vs = values();
            String[] ss = new String[vs.length];
            for (int i = 0; i < vs.length; i++) {
                ss[i] = vs[i].toString();
            }
            return ss;
        }
    }



}