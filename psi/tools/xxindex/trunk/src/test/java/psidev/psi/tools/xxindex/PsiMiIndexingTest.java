/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package psidev.psi.tools.xxindex;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;
import psidev.psi.tools.xxindex.index.StandardXpathIndex;
import psidev.psi.tools.xxindex.index.ByteRange;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * PSI-MI indexer tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 0.1
 */
public class PsiMiIndexingTest {

    @Test
    public void indexFile() throws Exception {
        final String f = PsiMiIndexingTest.class.getResource( "/10068665.xml" ).getFile();
        Assert.assertNotNull( f );
        File file = new File( f );

        final StandardXpathIndex index = XmlXpathIndexer.buildIndex( new FileInputStream( f ) );
        Assert.assertNotNull( index );
        
        Assert.assertFalse( index.containsXpath( "/foobar" ) );
        
        Assert.assertTrue( index.containsXpath( "/entrySet" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry/experimentList/experimentDescription" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry/interactorList/interactor" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry/interactionList/interaction" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry/interactionList/interaction/participantList/participant" ) );
        Assert.assertTrue( index.containsXpath( "/entrySet/entry/interactionList/interaction/participantList/participant/featureList/feature" ) );

        // check that main elements have been indexed
        Assert.assertEquals( 2, index.getRangeCount( "/entrySet/entry/experimentList/experimentDescription" ));
        Assert.assertEquals( 5, index.getRangeCount( "/entrySet/entry/interactorList/interactor" ));
        Assert.assertEquals( 7, index.getRangeCount( "/entrySet/entry/interactionList/interaction" ) );
        Assert.assertEquals( 14, index.getRangeCount( "/entrySet/entry/interactionList/interaction/participantList/participant" ) );
        Assert.assertEquals( 10, index.getRangeCount( "/entrySet/entry/interactionList/interaction/participantList/participant/featureList/feature" ) );

        // now let's extract a few stuffs
        StandardXmlElementExtractor xee = new StandardXmlElementExtractor();
        final List<ByteRange> ranges = index.getRange( "/entrySet/entry/interactionList/interaction" );
        Assert.assertEquals( 7, ranges.size() );
        final ByteRange range = ranges.get( 0 );
        final String xml = xee.readByteRange( range, file );
    }
}
