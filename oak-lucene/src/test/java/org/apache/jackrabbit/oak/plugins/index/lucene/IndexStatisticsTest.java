begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|IndexStatistics
operator|.
name|SYNTHETICALLY_FALLIABLE_FIELD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|IndexStatisticsTest
block|{
annotation|@
name|After
specifier|public
name|void
name|resetFailFlags
parameter_list|()
block|{
name|IndexStatistics
operator|.
name|failReadingFields
operator|=
literal|false
expr_stmt|;
name|IndexStatistics
operator|.
name|failReadingSyntheticallyFalliableField
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|numDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|numDocsWithDelele
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
literal|2
argument_list|)
decl_stmt|;
block|{
name|IndexWriter
name|writer
init|=
name|getWriter
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSimpleFieldDocCnt
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getSimpleFieldDocCntWithDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
literal|2
argument_list|)
decl_stmt|;
block|{
name|IndexWriter
name|writer
init|=
name|getWriter
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Stats don't need to get accurate result which might require reading more"
argument_list|,
literal|2
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|absentFields
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"absent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|":someHiddenField"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
name|FieldNames
operator|.
name|ANALYZED_FIELD_PREFIX
operator|+
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT_RELATIVE_NODE
operator|+
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo_facet"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onlyPropertyFields
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"manualBar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|":someHiddenField"
argument_list|,
literal|"manualBar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FieldNames
operator|.
name|ANALYZED_FIELD_PREFIX
operator|+
literal|"foo"
argument_list|,
literal|"manualBar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT_RELATIVE_NODE
operator|+
literal|"foo"
argument_list|,
literal|"manualBar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo_facet"
argument_list|,
literal|"manualBar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|Directory
name|d
init|=
name|createSampleDirectory
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"absent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|":someHiddenField"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
name|FieldNames
operator|.
name|ANALYZED_FIELD_PREFIX
operator|+
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT_RELATIVE_NODE
operator|+
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo_facet"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|unableToIterateFields
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexStatistics
operator|.
name|failReadingFields
operator|=
literal|true
expr_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|createSampleDirectory
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|stats
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|unableToReadCountForJcrTitle
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexStatistics
operator|.
name|failReadingSyntheticallyFalliableField
operator|=
literal|true
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|SYNTHETICALLY_FALLIABLE_FIELD
argument_list|,
literal|"title"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|IndexStatistics
name|stats
init|=
name|getStats
argument_list|(
name|createSampleDirectory
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stats
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
name|SYNTHETICALLY_FALLIABLE_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getDocCountFor
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Directory
name|createSampleDirectory
parameter_list|(
name|long
name|numOfDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createSampleDirectory
argument_list|(
name|numOfDocs
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Directory
name|createSampleDirectory
parameter_list|(
name|Document
name|moreDoc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createSampleDirectory
argument_list|(
literal|2
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|moreDoc
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Directory
name|createSampleDirectory
parameter_list|(
name|long
name|numOfDocs
parameter_list|,
name|Iterable
argument_list|<
name|Document
argument_list|>
name|moreDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|docs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|moreDocs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|createSampleDirectory
argument_list|(
name|docs
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Directory
name|createSampleDirectory
parameter_list|(
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|getWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|IndexWriter
name|getWriter
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|LuceneIndexConstants
operator|.
name|ANALYZER
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|config
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|IndexStatistics
name|getStats
parameter_list|(
name|Directory
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
comment|// no more reads
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexStatistics
name|stats
init|=
operator|new
name|IndexStatistics
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|//close reader... Index stats would read numDocs right away
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|stats
return|;
block|}
block|}
end_class

end_unit

