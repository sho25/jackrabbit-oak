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
name|jcr
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Node
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
name|FixturesHelper
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
name|FixturesHelper
operator|.
name|Fixture
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
name|IndexConstants
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
name|IndexEditorProvider
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|LoggerContext
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|spi
operator|.
name|ILoggingEvent
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|spi
operator|.
name|IThrowableProxy
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|Appender
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|filter
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|read
operator|.
name|ListAppender
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|spi
operator|.
name|FilterReply
import|;
end_import

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
name|ImmutableSet
import|;
end_import

begin_class
specifier|public
class|class
name|AsyncConflictsIT
extends|extends
name|DocumentClusterIT
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Fixture
argument_list|>
name|FIXTURES
init|=
name|FixturesHelper
operator|.
name|getFixtures
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_DEF_NODE
init|=
literal|"asyncconflict"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_PROPERTY
init|=
literal|"number"
decl_stmt|;
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
name|AsyncConflictsIT
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumptions
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|FIXTURES
operator|.
name|contains
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_NS
argument_list|)
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
name|OakMongoNSRepositoryStub
operator|.
name|isMongoDBAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updates
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|generator
init|=
operator|new
name|Random
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|ListAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|logAppender
init|=
name|subscribeAppender
argument_list|()
decl_stmt|;
name|setUpCluster
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|,
name|mks
argument_list|,
name|repos
argument_list|,
name|NOT_PROVIDED
argument_list|)
expr_stmt|;
name|defineIndex
argument_list|(
name|repos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numberNodes
init|=
literal|10000
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"adding {} nodes"
argument_list|,
name|numberNodes
argument_list|)
expr_stmt|;
name|Session
name|s
init|=
name|repos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
name|Node
name|test
init|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|test
operator|.
name|setPrimaryType
argument_list|(
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberNodes
condition|;
name|i
operator|++
control|)
block|{
name|test
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
expr_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
name|INDEX_PROPERTY
argument_list|,
name|generator
operator|.
name|nextInt
argument_list|(
name|numberNodes
operator|/
literal|3
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|1024
operator|==
literal|0
condition|)
block|{
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Nodes added."
argument_list|)
expr_stmt|;
comment|// issuing re-index
name|LOG
operator|.
name|info
argument_list|(
literal|"issuing re-index and wait for finish"
argument_list|)
expr_stmt|;
name|s
operator|=
name|repos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|index
init|=
name|s
operator|.
name|getNode
argument_list|(
literal|"/oak:index/"
operator|+
name|INDEX_DEF_NODE
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|isReindexFinished
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
name|raiseExceptions
argument_list|(
name|exceptions
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
comment|// if following fails it means the Async index failed at least once.
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"We should have not any '%s' in the logs"
argument_list|,
name|AsyncLogFilter
operator|.
name|MESSAGE
argument_list|)
argument_list|,
name|logAppender
operator|.
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|unsubscribe
argument_list|(
name|logAppender
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isReindexFinished
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|repos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
try|try
block|{
name|boolean
name|reindex
init|=
name|s
operator|.
name|getNode
argument_list|(
literal|"/oak:index/"
operator|+
name|INDEX_DEF_NODE
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|.
name|getBoolean
argument_list|()
decl_stmt|;
return|return
operator|!
name|reindex
return|;
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|defineIndex
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Repository
name|repo
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|repo
operator|.
name|login
argument_list|(
name|ADMIN
argument_list|)
decl_stmt|;
try|try
block|{
name|Node
name|n
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
name|INDEX_DEF_NODE
argument_list|)
expr_stmt|;
name|n
operator|.
name|setPrimaryType
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"compatVersion"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
literal|"indexRules"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setPrimaryType
argument_list|(
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
literal|"properties"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setPrimaryType
argument_list|(
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
literal|"number"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setPrimaryType
argument_list|(
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"propertyIndex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
name|INDEX_PROPERTY
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|IndexEditorProvider
argument_list|>
name|additionalIndexEditorProviders
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|(
name|IndexEditorProvider
operator|)
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isAsyncIndexing
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|private
name|ListAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|subscribeAppender
parameter_list|()
block|{
name|Filter
argument_list|<
name|ILoggingEvent
argument_list|>
name|filter
init|=
operator|new
name|AsyncLogFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|start
argument_list|()
expr_stmt|;
name|ListAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|appender
init|=
operator|new
name|ListAppender
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|()
decl_stmt|;
name|appender
operator|.
name|setContext
argument_list|(
operator|(
name|LoggerContext
operator|)
name|LoggerFactory
operator|.
name|getILoggerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|appender
operator|.
name|setName
argument_list|(
literal|"asynclogcollector"
argument_list|)
expr_stmt|;
name|appender
operator|.
name|addFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|appender
operator|.
name|start
argument_list|()
expr_stmt|;
operator|(
operator|(
name|LoggerContext
operator|)
name|LoggerFactory
operator|.
name|getILoggerFactory
argument_list|()
operator|)
operator|.
name|getLogger
argument_list|(
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
operator|.
name|ROOT_LOGGER_NAME
argument_list|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
return|return
name|appender
return|;
block|}
specifier|private
name|void
name|unsubscribe
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Appender
argument_list|<
name|ILoggingEvent
argument_list|>
name|appender
parameter_list|)
block|{
operator|(
operator|(
name|LoggerContext
operator|)
name|LoggerFactory
operator|.
name|getILoggerFactory
argument_list|()
operator|)
operator|.
name|getLogger
argument_list|(
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
operator|.
name|ROOT_LOGGER_NAME
argument_list|)
operator|.
name|detachAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|AsyncLogFilter
extends|extends
name|Filter
argument_list|<
name|ILoggingEvent
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MESSAGE
init|=
literal|"Unresolved conflicts in /:async"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|FilterReply
name|decide
parameter_list|(
name|ILoggingEvent
name|event
parameter_list|)
block|{
specifier|final
name|IThrowableProxy
name|tp
init|=
name|event
operator|.
name|getThrowableProxy
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|.
name|isGreaterOrEqual
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|&&
name|tp
operator|!=
literal|null
operator|&&
name|tp
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|MESSAGE
argument_list|)
condition|)
block|{
return|return
name|FilterReply
operator|.
name|ACCEPT
return|;
block|}
else|else
block|{
return|return
name|FilterReply
operator|.
name|DENY
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

