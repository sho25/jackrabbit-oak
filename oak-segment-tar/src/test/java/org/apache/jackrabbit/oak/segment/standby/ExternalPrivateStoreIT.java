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
name|segment
operator|.
name|standby
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
name|oak
operator|.
name|segment
operator|.
name|SegmentTestUtils
operator|.
name|createTmpTargetDir
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
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
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalPrivateStoreIT
extends|extends
name|DataStoreTestBase
block|{
specifier|private
name|File
name|primaryStore
decl_stmt|;
specifier|private
name|File
name|secondaryStore
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|closeServerAndClient
argument_list|()
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|primaryStore
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|secondaryStore
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
annotation|@
name|Override
specifier|protected
name|FileStore
name|setupPrimary
parameter_list|(
name|File
name|d
parameter_list|,
name|ScheduledExecutorService
name|primaryExecutor
parameter_list|)
throws|throws
name|Exception
block|{
name|primaryStore
operator|=
name|createTmpTargetDir
argument_list|(
literal|"ExternalStoreITPrimary"
argument_list|)
expr_stmt|;
return|return
name|setupFileDataStore
argument_list|(
name|d
argument_list|,
name|primaryStore
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|primaryExecutor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|FileStore
name|setupSecondary
parameter_list|(
name|File
name|d
parameter_list|,
name|ScheduledExecutorService
name|secondaryExecutor
parameter_list|)
throws|throws
name|Exception
block|{
name|secondaryStore
operator|=
name|createTmpTargetDir
argument_list|(
literal|"ExternalStoreITSecondary"
argument_list|)
expr_stmt|;
return|return
name|setupFileDataStore
argument_list|(
name|d
argument_list|,
name|secondaryStore
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|secondaryExecutor
argument_list|)
return|;
block|}
block|}
end_class

end_unit

