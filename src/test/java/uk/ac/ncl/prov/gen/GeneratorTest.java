package uk.ac.ncl.prov.gen;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openprovenance.prov.java.JProvUtility;
import org.openprovenance.prov.java.NSBundle;
import org.openprovenance.prov.java.component4.Bundle;

import uk.ac.ncl.prov.gen.generator.Generator;

/**
 * Unit test for Activity class
 */
public class GeneratorTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GeneratorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GeneratorTest.class );
    }
    
    public void testParsing() {
    	try {
			JProvUtility u = new JProvUtility();
			NSBundle b = u.convertASNToJava("../prov-gen/src/test/resources/prov/dissertation.provn");
			Generator generator = new Generator(b);
			generator.seed(true);
			System.out.println(generator.expand());
		} catch (Exception e) { 
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
    }
   
}
