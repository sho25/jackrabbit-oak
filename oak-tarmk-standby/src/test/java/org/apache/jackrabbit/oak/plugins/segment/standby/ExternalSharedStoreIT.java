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
name|commons
operator|.
name|CIHelper
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
name|Before
import|;
end_import

begin_class
specifier|public
class|class
name|ExternalSharedStoreIT
extends|extends
name|DataStoreTestBase
block|{
specifier|private
name|File
name|externalStore
decl_stmt|;
specifier|public
name|ExternalSharedStoreIT
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|storesCanBeEqual
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|checkEnvironment
parameter_list|()
block|{
name|assumeTrue
argument_list|(
operator|!
name|CIHelper
operator|.
name|travis
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|externalStore
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
parameter_list|)
throws|throws
name|IOException
block|{
name|externalStore
operator|=
name|createTmpTargetDir
argument_list|(
literal|"ExternalCommonStoreIT"
argument_list|)
expr_stmt|;
return|return
name|setupFileDataStore
argument_list|(
name|d
argument_list|,
name|externalStore
operator|.
name|getAbsolutePath
argument_list|()
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|setupFileDataStore
argument_list|(
name|d
argument_list|,
name|externalStore
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

