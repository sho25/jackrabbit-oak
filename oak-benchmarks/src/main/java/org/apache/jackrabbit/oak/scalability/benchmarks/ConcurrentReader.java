begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|scalability
operator|.
name|benchmarks
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
name|jackrabbit
operator|.
name|commons
operator|.
name|JcrUtils
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityAbstractSuite
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityAbstractSuite
operator|.
name|ExecutionContext
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
name|javax
operator|.
name|jcr
operator|.
name|Credentials
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
name|java
operator|.
name|util
operator|.
name|List
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
name|UUID
import|;
end_import

begin_comment
comment|/**  * Reads random paths concurrently with multiple readers/writers configured with {@link #WRITERS} and {@link #READERS}.  *  *<p>  * The following system JVM properties can be defined to configure the benchmark behavior.  *<ul>  *<li>  *<code>concurrentWriters</code> - Controls the number of concurrent background threads for writing nodes.  *     Defaults to 0.  *</li>  *<li>  *<code>concurrentReaders</code> - Controls the number of concurrent background threads for reading nodes.  *     Defaults to 0.  *</li>  *<li>  *<code>assets</code> - Controls the number of nodes to read/write in the background threads.  *     Defaults to 100.  *</li>  *</ul>  *  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentReader
extends|extends
name|ScalabilityBenchmark
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConcurrentReader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|WRITERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"concurrentReaders"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|READERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"concurrentWriters"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ASSETS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"assets"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
name|ConcurrentReader
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Thread
argument_list|>
name|jobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeExecute
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|session
operator|.
name|getRootNode
argument_list|()
argument_list|,
name|ROOT_NODE_NAME
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|WRITERS
condition|;
name|idx
operator|++
control|)
block|{
try|try
block|{
name|Thread
name|thread
init|=
name|createJob
argument_list|(
operator|new
name|Writer
argument_list|(
literal|"concurrentWriter-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|+
name|idx
argument_list|,
name|MAX_ASSETS
argument_list|,
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
argument_list|,
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|jobs
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
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
literal|"error creating background writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|READERS
condition|;
name|idx
operator|++
control|)
block|{
try|try
block|{
name|Thread
name|thread
init|=
name|createJob
argument_list|(
operator|new
name|Reader
argument_list|(
literal|"concurrentReader-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|+
name|idx
argument_list|,
name|MAX_ASSETS
argument_list|,
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
argument_list|,
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|jobs
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
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
literal|"error creating background reader"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|running
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|Thread
name|createJob
parameter_list|(
specifier|final
name|Job
name|job
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|job
operator|.
name|id
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
name|job
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterExecute
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|jobs
control|)
block|{
try|try
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error stopping thread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|jobs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|,
literal|100
argument_list|,
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|reader
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
specifier|abstract
class|class
name|Job
block|{
specifier|final
name|Node
name|parent
decl_stmt|;
specifier|final
name|Session
name|session
decl_stmt|;
specifier|final
name|String
name|id
decl_stmt|;
specifier|final
name|int
name|maxAssets
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|readPaths
decl_stmt|;
specifier|final
name|Random
name|rand
decl_stmt|;
name|Job
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|maxAssets
parameter_list|,
name|Session
name|session
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|maxAssets
operator|=
name|maxAssets
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|addNode
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|readPaths
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|get
argument_list|(
name|ScalabilityAbstractSuite
operator|.
name|CTX_SEARCH_PATHS_PROP
argument_list|)
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|process
parameter_list|()
function_decl|;
block|}
comment|/**      * Simple Reader job      */
class|class
name|Reader
extends|extends
name|Job
block|{
name|Reader
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|maxAssets
parameter_list|,
name|Session
name|session
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|id
argument_list|,
name|maxAssets
argument_list|,
name|session
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|()
block|{
try|try
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
name|int
name|readPathSize
init|=
name|readPaths
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|count
operator|<=
name|maxAssets
condition|)
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|JcrUtils
operator|.
name|getNodeIfExists
argument_list|(
name|readPaths
operator|.
name|get
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|readPathSize
argument_list|)
argument_list|)
argument_list|,
name|session
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
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
literal|"Exception in reading"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Simple Writer job      */
class|class
name|Writer
extends|extends
name|Job
block|{
name|Writer
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|maxAssets
parameter_list|,
name|Session
name|session
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|id
argument_list|,
name|maxAssets
argument_list|,
name|session
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|()
block|{
try|try
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|count
operator|<=
name|maxAssets
condition|)
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|parent
argument_list|,
literal|"Node"
operator|+
name|count
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"prop1"
argument_list|,
literal|"val1"
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"prop2"
argument_list|,
literal|"val2"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
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
literal|"Exception in write"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
