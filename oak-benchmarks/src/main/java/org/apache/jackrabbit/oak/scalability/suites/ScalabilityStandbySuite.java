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
name|suites
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|fixture
operator|.
name|OakFixture
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
name|fixture
operator|.
name|SegmentTarFixture
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
name|ScalabilitySuite
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
name|benchmarks
operator|.
name|ScalabilityBenchmark
import|;
end_import

begin_comment
comment|/**  * This suite test will set up a primary instance and a standby instance. It  * will create<code>nodeCount</code> nodes on primary, to be synced on the  * standby. It is the responsibility of the test to start the standby process  * and to query different JMX MBeans for asserting benchmark duration.  *   *<p>  * In order to obtain meaningful results, please note that the  *<code>noWarmup</code> JVM property needs to be set to<code>true</code>. This  * way "false" sync cycles taking up only a few milliseconds are avoided.  *   *<p>  * The following system JVM properties can be defined to configure the suite.  *   *<ul>  *<li><code>nodeCount</code> - Controls the number of nodes to be created on  * the primary. Defaults to 100_000.</li>  *</ul>  *  */
end_comment

begin_class
specifier|public
class|class
name|ScalabilityStandbySuite
extends|extends
name|ScalabilityAbstractSuite
block|{
comment|/**      * Number of nodes to be created on primary.      */
specifier|private
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"nodeCount"
argument_list|,
literal|100_000
argument_list|)
decl_stmt|;
comment|/**      * Iteration counter      */
specifier|private
name|int
name|iteration
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ScalabilitySuite
name|addBenchmarks
parameter_list|(
name|ScalabilityBenchmark
modifier|...
name|benchmarks
parameter_list|)
block|{
for|for
control|(
name|ScalabilityBenchmark
name|sb
range|:
name|benchmarks
control|)
block|{
name|this
operator|.
name|benchmarks
operator|.
name|put
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|RepositoryFixture
name|fixture
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|(
name|repository
argument_list|,
name|fixture
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
operator|)
condition|)
block|{
return|return;
block|}
name|OakRepositoryFixture
name|orf
init|=
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
decl_stmt|;
name|SegmentTarFixture
name|stf
init|=
operator|(
name|SegmentTarFixture
operator|)
name|orf
operator|.
name|getOakFixture
argument_list|()
decl_stmt|;
if|if
condition|(
name|orf
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|OakFixture
operator|.
name|OAK_SEGMENT_TAR_COLD
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|contextMap
init|=
name|context
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
literal|"clientSyncs"
argument_list|,
name|stf
operator|.
name|getClientSyncs
argument_list|()
argument_list|)
expr_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
literal|"serverSyncs"
argument_list|,
name|stf
operator|.
name|getServerSyncs
argument_list|()
argument_list|)
expr_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
literal|"stores"
argument_list|,
name|stf
operator|.
name|getStores
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot run ScalabilityStandbySuite on current fixture. Use Oak-Segment-Tar-Cold instead!"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeIteration
parameter_list|(
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|Node
name|rootFolder
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"rootFolder"
operator|+
name|iteration
operator|++
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
name|createNodes
argument_list|(
name|rootFolder
argument_list|,
name|NODE_COUNT
argument_list|,
operator|new
name|Random
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|executeBenchmark
parameter_list|(
name|ScalabilityBenchmark
name|benchmark
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Started pre benchmark hook : {}"
argument_list|,
name|benchmark
argument_list|)
expr_stmt|;
name|benchmark
operator|.
name|beforeExecute
argument_list|(
name|getRepository
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started execution : {}"
argument_list|,
name|benchmark
argument_list|)
expr_stmt|;
if|if
condition|(
name|PROFILE
condition|)
block|{
name|context
operator|.
name|startProfiler
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|benchmark
operator|.
name|execute
argument_list|(
name|getRepository
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|,
name|context
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
literal|"Exception in benchmark execution "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|stopProfiler
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started post benchmark hook : {}"
argument_list|,
name|benchmark
argument_list|)
expr_stmt|;
name|benchmark
operator|.
name|afterExecute
argument_list|(
name|getRepository
argument_list|()
argument_list|,
name|CREDENTIALS
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
specifier|static
name|void
name|createNodes
parameter_list|(
name|Node
name|parent
parameter_list|,
name|int
name|nodeCount
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|blobSize
init|=
literal|5
operator|*
literal|1024
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|nodeCount
operator|/
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|folder
init|=
name|parent
operator|.
name|addNode
argument_list|(
literal|"Folder#"
operator|+
name|j
argument_list|,
literal|"nt:folder"
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
operator|(
name|nodeCount
operator|<
literal|1000
condition|?
name|nodeCount
else|:
literal|1000
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|file
init|=
name|folder
operator|.
name|addNode
argument_list|(
literal|"server"
operator|+
name|i
argument_list|,
literal|"nt:file"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|blobSize
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Node
name|content
init|=
name|file
operator|.
name|addNode
argument_list|(
literal|"jcr:content"
argument_list|,
literal|"nt:resource"
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:mimeType"
argument_list|,
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
