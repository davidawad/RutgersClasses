import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class to read rating specifications as XML documents
 * using SAX interface.
 * 
 * Fleshes out a few of the SAX callback methods to 
 * create a rating table structure corresponding
 * to the specification in the input file.
 * 
 * Note: this is used only for the test data from 
 * the Segaran book "Programming Collective Intelligence".
 * Other functionality is used for loading in the 
 * MovieLens dataset.
 * 
 * @author Tarek El-Gaaly and Matthew Stone
 * @version 1.0
 *
 */
public class TableReader extends DefaultHandler {

    private RatingTable ratingTable;
    
    /** Used to construct error messages based on file position */
    private Locator locator;
    
    /* Allows us to allocate unique identifiers to new objects */
    static int nextId = 1;
    
    /**
     * @return the data set read in 
     */
    public RatingTable getRatingData() {
        return ratingTable;
    }
    
    /**
     * Constructor
     * 
     */
    public TableReader() {
        super();
    }

    /**
     *  called when XML parser starts reading and gives us tabs 
     *  on the dynamic Locator object parameter
     *  
     *  @param locator updated by XML parser to hold current file location
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Pretty print the current position in the source file for
     * error messages.
     * 
     * @return String specification of current position in locator
     */
    static public String locationMsg(Locator locator) {
        String location = "";
        if (locator != null) {
            String id = locator.getSystemId();
            if (id != null)
                location = id; // XML-document name;
            location += " line " + locator.getLineNumber();
            location += ", column " + locator.getColumnNumber();
            location += ": ";
        }
        return location;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element.
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * 
     */
    static public String getStringParam(Attributes atts, String param, String defaultValue, Locator locator) 
    {
        String result = defaultValue;
        String value = atts.getValue(RatingDictionary.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            result = value;
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * @throws SAXException when value should be but is not an integer
     */
    static public int getIntParam(Attributes atts, String param, int defaultValue, Locator locator) 
    throws SAXException {
        int result = defaultValue;
        String value = atts.getValue(RatingDictionary.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            try {
                result = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // compose a text with location of error-case:
                throw new SAXException(locationMsg(locator) + "Bad integer format of " + value + " for " + param);
            } 
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * @throws SAXException when value should be but is not a double
     */
    static public double getDoubleParam(Attributes atts, String param, double defaultValue, Locator locator) 
    throws SAXException {
        double result = defaultValue;
        String value = atts.getValue(RatingDictionary.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            try {
                result = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // compose a text with location of error-case:
                throw new SAXException(locationMsg(locator) + "Bad double format of " + value + " for " + param);
            } 
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     */
    static public boolean getBoolParam(Attributes atts, String param, boolean defaultValue, Locator locator) 
    throws SAXException {
        boolean result = defaultValue;
        String value = atts.getValue(RatingDictionary.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            result = Boolean.parseBoolean(value);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////
    // Event handlers.
    ////////////////////////////////////////////////////////////////////

    public void startElement (String uri, String name,
                  String qName, Attributes atts) 
    throws SAXException
    {
        if (!RatingDictionary.XMLNS.equals(uri) && !"".equals (uri)) {
            System.err.println(locationMsg(locator) + "Namespace: {" + uri + "}" + " not recognized");
            return;
        }
        
        if (RatingDictionary.XML_NAME.equals(name)) {
            ratingTable = new RatingTable(null, RatingTable.CommonAttribute.NONE);
            return;
        }
        
        if (Rating.XML_NAME.equals(name)) {
            String criticName = getStringParam(atts, "criticName", "null", locator);
            String movieName = getStringParam(atts, "movie", "null", locator);
            double score = getDoubleParam(atts, "score", -1.0, locator);
            
            Rating r = new Rating(criticName, movieName, score);
            ratingTable.addRating(r);
            
            return;
        }       
    }
}
