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
name|File
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ServerAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|IMongodConfig
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
name|MongoCmdOptionsBuilder
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
name|MongodConfigBuilder
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
name|Net
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
name|Storage
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
name|distribution
operator|.
name|Versions
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
name|IStopable
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

begin_comment
comment|/**  * Helper class for starting/stopping a mongod process.  */
end_comment

begin_class
specifier|public
class|class
name|MongodProcess
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"3.4.10"
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
specifier|private
name|IStopable
name|process
decl_stmt|;
specifier|private
specifier|final
name|MongodStarter
name|starter
decl_stmt|;
specifier|private
specifier|final
name|IMongodConfig
name|config
decl_stmt|;
name|MongodProcess
parameter_list|(
name|MongodStarter
name|starter
parameter_list|,
name|String
name|rsName
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|starter
operator|=
name|starter
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|createConfiguration
argument_list|(
name|rsName
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already started"
argument_list|)
throw|;
block|}
name|process
operator|=
name|starter
operator|.
name|prepare
argument_list|(
name|config
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|process
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already stopped"
argument_list|)
throw|;
block|}
name|process
operator|.
name|stop
argument_list|()
expr_stmt|;
name|process
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|process
operator|==
literal|null
return|;
block|}
specifier|public
name|ServerAddress
name|getAddress
parameter_list|()
block|{
return|return
operator|new
name|ServerAddress
argument_list|(
name|config
operator|.
name|net
argument_list|()
operator|.
name|getBindIp
argument_list|()
argument_list|,
name|config
operator|.
name|net
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|IMongodConfig
name|createConfiguration
parameter_list|(
name|String
name|rsName
parameter_list|,
name|int
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MongodConfigBuilder
argument_list|()
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|withFeatures
argument_list|(
parameter_list|()
lambda|->
name|VERSION
argument_list|)
argument_list|)
operator|.
name|net
argument_list|(
operator|new
name|Net
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|p
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|replication
argument_list|(
name|newStorage
argument_list|(
name|p
argument_list|,
name|rsName
argument_list|)
argument_list|)
comment|// enable journal
operator|.
name|cmdOptions
argument_list|(
operator|new
name|MongoCmdOptionsBuilder
argument_list|()
operator|.
name|useNoJournal
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|Storage
name|newStorage
parameter_list|(
name|int
name|port
parameter_list|,
name|String
name|rs
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|dbPath
init|=
operator|new
name|File
argument_list|(
name|TMP_DIR
operator|.
name|asFile
argument_list|()
argument_list|,
literal|"mongod-"
operator|+
name|port
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbPath
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dbPath
argument_list|)
expr_stmt|;
block|}
name|int
name|oplogSize
init|=
name|rs
operator|!=
literal|null
condition|?
literal|512
else|:
literal|0
decl_stmt|;
return|return
operator|new
name|Storage
argument_list|(
name|dbPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|rs
argument_list|,
name|oplogSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit
