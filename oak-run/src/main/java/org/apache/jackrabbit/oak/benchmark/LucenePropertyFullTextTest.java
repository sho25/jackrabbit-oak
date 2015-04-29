begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|benchmark
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
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
name|api
operator|.
name|Type
operator|.
name|BOOLEAN
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
name|api
operator|.
name|Type
operator|.
name|LONG
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|COMPAT_MODE
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
name|INDEX_RULES
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
name|PROP_NAME
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
name|PROP_NODE
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
name|PROP_PROPERTY_INDEX
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
name|TYPE_LUCENE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|Oak
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Tree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|benchmark
operator|.
name|wikipedia
operator|.
name|WikipediaImport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|fixture
operator|.
name|JcrCreator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|fixture
operator|.
name|OakRepositoryFixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|fixture
operator|.
name|RepositoryFixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|jcr
operator|.
name|Jcr
import|;
end_import

begin_import
import|import
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
name|LuceneIndexEditorProvider
import|;
end_import

begin_import
import|import
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
name|LuceneIndexProvider
import|;
end_import

begin_import
import|import
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
name|util
operator|.
name|LuceneInitializerHelper
import|;
end_import

begin_import
import|import
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
name|tree
operator|.
name|TreeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|Observer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|lifecycle
operator|.
name|RepositoryInitializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * Perform a benchmark on how long it takes for an ingested item to be available in a Lucene  * Property index when indexed in conjunction with a Global full-text lucene (same thread). It makes  * use of the {@link WikipediaImport} to use a Wikipedia dump for content injestion.  *</p>  *<p>  * Suggested dump:   * {@linkplain https://dumps.wikimedia.org/enwiki/20150403/enwiki-20150403-pages-articles.xml.bz2}  *</p>  *<p>  * Usage example:  *</p>  *   *<pre>  * java -Druntime=900 -Dlogback.configurationFile=logback-benchmark.xml \  *      -jar ~/.m2/repository/org/apache/jackrabbit/oak-run/1.4-SNAPSHOT/oak-run-1.4-SNAPSHOT.jar \  *      benchmark --wikipedia enwiki-20150403-pages-articles.xml.bz2 \  *      --base ~/tmp/oak/ LucenePropertyFullTextTest Oak-Tar Oak-Mongo  *</pre>  *<p>  * it will run the benchmark for 15 minutes against TarNS and MongoNS.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|LucenePropertyFullTextTest
extends|extends
name|AbstractTest
argument_list|<
name|LucenePropertyFullTextTest
operator|.
name|TestContext
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LucenePropertyFullTextTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|WikipediaImport
name|importer
decl_stmt|;
specifier|private
name|Thread
name|asyncImporter
decl_stmt|;
specifier|private
name|boolean
name|benchmarkCompleted
decl_stmt|,
name|importerCompleted
decl_stmt|;
name|Boolean
name|storageEnabled
decl_stmt|;
name|String
name|currentFixture
decl_stmt|,
name|currentTest
decl_stmt|;
comment|/**      * context used across the tests      */
class|class
name|TestContext
block|{
specifier|final
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
specifier|final
name|String
name|title
decl_stmt|;
specifier|public
name|TestContext
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|title
parameter_list|)
block|{
name|this
operator|.
name|title
operator|=
name|checkNotNull
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * helper class to initialise the Lucene Property index definition      */
specifier|static
class|class
name|LucenePropertyInitialiser
implements|implements
name|RepositoryInitializer
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|properties
decl_stmt|;
specifier|public
name|LucenePropertyInitialiser
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|checkNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isAlreadyThere
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeBuilder
name|root
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|root
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|&&
name|root
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
specifier|final
name|NodeBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAlreadyThere
argument_list|(
name|builder
argument_list|)
condition|)
block|{
name|Tree
name|t
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|t
operator|=
name|t
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|COMPAT_MODE
argument_list|,
literal|2L
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE_LUCENE
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|t
operator|=
name|t
operator|.
name|addChild
argument_list|(
name|INDEX_RULES
argument_list|)
expr_stmt|;
name|t
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|=
name|t
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
expr_stmt|;
name|Tree
name|propnode
init|=
name|t
operator|.
name|addChild
argument_list|(
name|PROP_NODE
argument_list|)
decl_stmt|;
name|propnode
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|propnode
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|properties
control|)
block|{
name|Tree
name|t1
init|=
name|propnode
operator|.
name|addChild
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|t1
operator|.
name|setProperty
argument_list|(
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|,
name|BOOLEAN
argument_list|)
expr_stmt|;
name|t1
operator|.
name|setProperty
argument_list|(
name|PROP_NAME
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * reference to the last added title. Used for looking up with queries.      */
specifier|private
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|lastTitle
init|=
operator|new
name|AtomicReference
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|LucenePropertyFullTextTest
parameter_list|(
specifier|final
name|File
name|dump
parameter_list|,
specifier|final
name|boolean
name|flat
parameter_list|,
specifier|final
name|boolean
name|doReport
parameter_list|,
specifier|final
name|Boolean
name|storageEnabled
parameter_list|)
block|{
name|this
operator|.
name|importer
operator|=
operator|new
name|WikipediaImport
argument_list|(
name|dump
argument_list|,
name|flat
argument_list|,
name|doReport
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|pageAdded
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Setting title: {}"
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|lastTitle
operator|.
name|set
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|storageEnabled
operator|=
name|storageEnabled
expr_stmt|;
name|this
operator|.
name|currentTest
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
name|currentFixture
operator|=
name|fixture
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
operator|new
name|LuceneInitializerHelper
argument_list|(
literal|"luceneGlobal"
argument_list|,
name|storageEnabled
argument_list|)
operator|)
operator|.
name|async
argument_list|()
argument_list|)
comment|// the WikipediaImporter set a property `title`
operator|.
name|with
argument_list|(
operator|new
name|LucenePropertyInitialiser
argument_list|(
literal|"luceneTitle"
argument_list|,
name|of
argument_list|(
literal|"title"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"beforeSuite() - {} - {}"
argument_list|,
name|currentFixture
argument_list|,
name|currentTest
argument_list|)
expr_stmt|;
name|benchmarkCompleted
operator|=
literal|false
expr_stmt|;
name|importerCompleted
operator|=
literal|false
expr_stmt|;
name|asyncImporter
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|importer
operator|.
name|importWikipedia
argument_list|(
name|loginWriter
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while importing the dump. Trying to halt everything."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|importerCompleted
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|benchmarkCompleted
condition|)
block|{
name|importerCompleted
operator|=
literal|true
expr_stmt|;
name|issueHaltRequest
argument_list|(
literal|"Wikipedia import completed."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|asyncImporter
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// allowing the async index to catch up.
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"afterSuite() - {} - {}"
argument_list|,
name|currentFixture
argument_list|,
name|currentTest
argument_list|)
expr_stmt|;
name|asyncImporter
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|lastTitle
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|runTest
argument_list|(
operator|new
name|TestContext
argument_list|(
name|lastTitle
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|(
specifier|final
name|TestContext
name|ec
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|importerCompleted
condition|)
block|{
return|return;
block|}
specifier|final
name|long
name|maxWait
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|long
name|waitUnit
init|=
literal|50
decl_stmt|;
name|long
name|sleptSoFar
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|performQuery
argument_list|(
name|ec
argument_list|)
operator|&&
name|sleptSoFar
operator|<
name|maxWait
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"title '{}' not found. Waiting and retry. sleptSoFar: {}ms"
argument_list|,
name|ec
operator|.
name|title
argument_list|,
name|sleptSoFar
argument_list|)
expr_stmt|;
name|sleptSoFar
operator|+=
name|waitUnit
expr_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|waitUnit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sleptSoFar
operator|<
name|maxWait
condition|)
block|{
comment|// means we exited the loop as we found it.
name|LOG
operator|.
name|info
argument_list|(
literal|"{} - {} - title '{}' found with a wait/try of {}ms"
argument_list|,
name|currentFixture
argument_list|,
name|currentTest
argument_list|,
name|ec
operator|.
name|title
argument_list|,
name|sleptSoFar
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} - {} - title '{}' timed out with a way/try of {}ms."
argument_list|,
name|currentFixture
argument_list|,
name|currentTest
argument_list|,
name|ec
operator|.
name|title
argument_list|,
name|sleptSoFar
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|performQuery
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|TestContext
name|ec
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|QueryManager
name|qm
init|=
name|ec
operator|.
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|ec
operator|.
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"SELECT * FROM [nt:base] WHERE [title] = $title"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
decl_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"title"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|ec
operator|.
name|title
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"statement: {} - title: {}"
argument_list|,
name|q
operator|.
name|getStatement
argument_list|()
argument_list|,
name|ec
operator|.
name|title
argument_list|)
expr_stmt|;
name|RowIterator
name|rows
init|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getRows
argument_list|()
decl_stmt|;
if|if
condition|(
name|rows
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|rows
operator|.
name|nextRow
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|issueHaltChildThreads
parameter_list|()
block|{
if|if
condition|(
operator|!
name|importerCompleted
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"benchmark completed. Issuing an halt for the importer"
argument_list|)
expr_stmt|;
name|benchmarkCompleted
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|importer
operator|.
name|issueHaltImport
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

