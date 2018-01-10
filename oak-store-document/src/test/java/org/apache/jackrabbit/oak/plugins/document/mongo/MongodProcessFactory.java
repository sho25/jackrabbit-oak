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
name|plugins
operator|.
name|document
operator|.
name|mongo
package|;
end_package

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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
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
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|mongo
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|mongo
operator|.
name|MongodStarter
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|mongo
operator|.
name|config
operator|.
name|DownloadConfigBuilder
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|mongo
operator|.
name|config
operator|.
name|ExtractedArtifactStoreBuilder
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|mongo
operator|.
name|config
operator|.
name|RuntimeConfigBuilder
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|process
operator|.
name|config
operator|.
name|IRuntimeConfig
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|process
operator|.
name|io
operator|.
name|directories
operator|.
name|FixedPath
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|process
operator|.
name|io
operator|.
name|directories
operator|.
name|IDirectory
import|;
end_import

begin_import
import|import
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|process
operator|.
name|runtime
operator|.
name|Network
import|;
end_import

begin_import
import|import static
name|de
operator|.
name|flapdoodle
operator|.
name|embed
operator|.
name|process
operator|.
name|io
operator|.
name|directories
operator|.
name|Directories
operator|.
name|join
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

begin_comment
comment|/**  * External resource for mongod processes.  */
end_comment

begin_class
specifier|public
class|class
name|MongodProcessFactory
extends|extends
name|ExternalResource
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
name|MongodProcessFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IDirectory
name|EXTRACT_DIR
init|=
name|join
argument_list|(
operator|new
name|FixedPath
argument_list|(
literal|"target"
argument_list|)
argument_list|,
operator|new
name|FixedPath
argument_list|(
literal|"mongo-extracted"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IDirectory
name|DOWNLOAD_DIR
init|=
name|join
argument_list|(
operator|new
name|FixedPath
argument_list|(
literal|"target"
argument_list|)
argument_list|,
operator|new
name|FixedPath
argument_list|(
literal|"mongo-download"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IDirectory
name|TMP_DIR
init|=
name|join
argument_list|(
operator|new
name|FixedPath
argument_list|(
literal|"target"
argument_list|)
argument_list|,
operator|new
name|FixedPath
argument_list|(
literal|"tmp"
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"de.flapdoodle.embed.io.tmpdir"
argument_list|,
name|TMP_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|IRuntimeConfig
name|CONFIG
init|=
operator|new
name|RuntimeConfigBuilder
argument_list|()
operator|.
name|defaultsWithLogger
argument_list|(
name|Command
operator|.
name|MongoD
argument_list|,
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MongodProcessFactory
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|artifactStore
argument_list|(
operator|new
name|ExtractedArtifactStoreBuilder
argument_list|()
operator|.
name|defaults
argument_list|(
name|Command
operator|.
name|MongoD
argument_list|)
operator|.
name|download
argument_list|(
operator|new
name|DownloadConfigBuilder
argument_list|()
operator|.
name|defaultsForCommand
argument_list|(
name|Command
operator|.
name|MongoD
argument_list|)
operator|.
name|artifactStorePath
argument_list|(
name|DOWNLOAD_DIR
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|extractDir
argument_list|(
name|EXTRACT_DIR
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|daemonProcess
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MongodStarter
name|STARTER
init|=
name|MongodStarter
operator|.
name|getInstance
argument_list|(
name|CONFIG
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|MongodProcess
argument_list|>
name|processes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|MongodProcess
argument_list|>
name|startReplicaSet
parameter_list|(
name|String
name|replicaSetName
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
name|ports
init|=
name|Network
operator|.
name|getFreeServerPorts
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
name|size
argument_list|)
decl_stmt|;
return|return
name|startReplicaSet
argument_list|(
name|replicaSetName
argument_list|,
name|ports
argument_list|)
return|;
block|}
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|MongodProcess
argument_list|>
name|startReplicaSet
parameter_list|(
name|String
name|replicaSetName
parameter_list|,
name|int
index|[]
name|ports
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|ports
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|MongodProcess
argument_list|>
name|executables
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|p
range|:
name|ports
control|)
block|{
name|MongodProcess
name|proc
init|=
operator|new
name|MongodProcess
argument_list|(
name|STARTER
argument_list|,
name|replicaSetName
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|proc
operator|.
name|start
argument_list|()
expr_stmt|;
name|processes
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|proc
argument_list|)
expr_stmt|;
name|executables
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|proc
argument_list|)
expr_stmt|;
block|}
name|initRS
argument_list|(
name|replicaSetName
argument_list|,
name|ports
argument_list|)
expr_stmt|;
return|return
name|executables
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|before
parameter_list|()
block|{
if|if
condition|(
operator|!
name|EXTRACT_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|EXTRACT_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|TMP_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|TMP_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|DOWNLOAD_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|DOWNLOAD_DIR
operator|.
name|asFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
name|processes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|port
parameter_list|,
name|process
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|process
operator|.
name|isStopped
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MongoDB on port {} already stopped"
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping MongoDB on port {}"
argument_list|,
name|port
argument_list|)
expr_stmt|;
try|try
block|{
name|process
operator|.
name|stop
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
literal|"Exception stopping MongoDB process"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|processes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|static
name|String
name|localhost
parameter_list|(
name|Integer
modifier|...
name|ports
parameter_list|)
block|{
return|return
name|localhost
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ports
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|String
name|localhost
parameter_list|(
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|ports
parameter_list|)
block|{
name|String
name|portsString
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|ports
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|portsString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|host
operator|+=
literal|":"
operator|+
name|portsString
expr_stmt|;
block|}
return|return
name|host
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
name|void
name|initRS
parameter_list|(
name|String
name|rs
parameter_list|,
name|int
index|[]
name|ports
parameter_list|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|members
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|ports
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|members
operator|.
name|add
argument_list|(
operator|new
name|Document
argument_list|(
literal|"_id"
argument_list|,
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"host"
argument_list|,
name|localhost
argument_list|(
name|ports
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Document
name|config
init|=
operator|new
name|Document
argument_list|(
literal|"_id"
argument_list|,
name|rs
argument_list|)
decl_stmt|;
name|config
operator|.
name|append
argument_list|(
literal|"members"
argument_list|,
name|members
argument_list|)
expr_stmt|;
try|try
init|(
name|MongoClient
name|c
init|=
operator|new
name|MongoClient
argument_list|(
name|localhost
argument_list|()
argument_list|,
name|ports
index|[
literal|0
index|]
argument_list|)
init|)
block|{
name|c
operator|.
name|getDatabase
argument_list|(
literal|"admin"
argument_list|)
operator|.
name|runCommand
argument_list|(
operator|new
name|Document
argument_list|(
literal|"replSetInitiate"
argument_list|,
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

