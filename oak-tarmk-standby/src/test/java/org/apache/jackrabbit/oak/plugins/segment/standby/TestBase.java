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
name|segment
operator|.
name|standby
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|SystemUtils
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|io
operator|.
name|IOException
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
name|segment
operator|.
name|SegmentTestUtils
operator|.
name|createTmpTargetDir
import|;
end_import

begin_class
specifier|public
class|class
name|TestBase
block|{
name|int
name|port
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"standby.server.port"
argument_list|,
literal|"52808"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|String
name|LOCALHOST
init|=
literal|"127.0.0.1"
decl_stmt|;
name|File
name|directoryS
decl_stmt|;
name|FileStore
name|storeS
decl_stmt|;
name|File
name|directoryC
decl_stmt|;
name|FileStore
name|storeC
decl_stmt|;
name|File
name|directoryC2
decl_stmt|;
name|FileStore
name|storeC2
decl_stmt|;
comment|/*      Java 6 on Windows doesn't support dual IP stacks, so we will skip our IPv6      tests.     */
specifier|protected
specifier|final
name|boolean
name|noDualStackSupport
init|=
name|SystemUtils
operator|.
name|IS_OS_WINDOWS
operator|&&
name|SystemUtils
operator|.
name|IS_JAVA_1_6
decl_stmt|;
specifier|public
name|void
name|setUpServerAndClient
parameter_list|()
throws|throws
name|IOException
block|{
comment|// server
name|directoryS
operator|=
name|createTmpTargetDir
argument_list|(
literal|"FailoverServerTest"
argument_list|)
expr_stmt|;
name|storeS
operator|=
name|setupPrimary
argument_list|(
name|directoryS
argument_list|)
expr_stmt|;
comment|// client
name|directoryC
operator|=
name|createTmpTargetDir
argument_list|(
literal|"FailoverClientTest"
argument_list|)
expr_stmt|;
name|storeC
operator|=
name|setupSecondary
argument_list|(
name|directoryC
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|FileStore
name|setupPrimary
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|FileStore
name|getPrimary
parameter_list|()
block|{
return|return
name|storeS
return|;
block|}
specifier|protected
name|FileStore
name|setupSecondary
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileStore
argument_list|(
name|directoryC
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|FileStore
name|getSecondary
parameter_list|()
block|{
return|return
name|storeC
return|;
block|}
specifier|protected
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
specifier|public
name|void
name|setUpServerAndTwoClients
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpServerAndClient
argument_list|()
expr_stmt|;
name|directoryC2
operator|=
name|createTmpTargetDir
argument_list|(
literal|"FailoverClient2Test"
argument_list|)
expr_stmt|;
name|storeC2
operator|=
operator|new
name|FileStore
argument_list|(
name|directoryC2
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|closeServerAndClient
parameter_list|()
block|{
name|storeS
operator|.
name|close
argument_list|()
expr_stmt|;
name|storeC
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directoryS
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directoryC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|closeServerAndTwoClients
parameter_list|()
block|{
name|closeServerAndClient
argument_list|()
expr_stmt|;
name|storeC2
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|directoryC2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

