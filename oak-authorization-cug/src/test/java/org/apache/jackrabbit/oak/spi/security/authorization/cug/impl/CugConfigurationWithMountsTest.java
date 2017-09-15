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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

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
name|AbstractSecurityTest
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
name|spi
operator|.
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_class
specifier|public
class|class
name|CugConfigurationWithMountsTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
name|CugConfiguration
name|createConfiguration
parameter_list|(
name|MountInfoProvider
name|mip
parameter_list|)
block|{
name|CugConfiguration
name|cugConfiguration
init|=
operator|new
name|CugConfiguration
argument_list|()
decl_stmt|;
name|cugConfiguration
operator|.
name|bindMountInfoProvider
argument_list|(
name|mip
argument_list|)
expr_stmt|;
name|cugConfiguration
operator|.
name|activate
argument_list|(
name|AbstractCugTest
operator|.
name|CUG_CONFIG
argument_list|)
expr_stmt|;
return|return
name|cugConfiguration
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMountAtCugSupportedPath
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|AbstractCugTest
operator|.
name|SUPPORTED_PATH
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createConfiguration
argument_list|(
name|mip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMountBelowCugSupportedPath
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|AbstractCugTest
operator|.
name|SUPPORTED_PATH
operator|+
literal|"/mount"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createConfiguration
argument_list|(
name|mip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMountAboveCugSupportedPath
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|AbstractCugTest
operator|.
name|SUPPORTED_PATH3
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createConfiguration
argument_list|(
name|mip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMountAtRootWithSupportedPaths
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createConfiguration
argument_list|(
name|mip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMountAtUnsupportedPath
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|AbstractCugTest
operator|.
name|UNSUPPORTED_PATH
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CugConfiguration
name|configuration
init|=
name|createConfiguration
argument_list|(
name|mip
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|AbstractCugTest
operator|.
name|SUPPORTED_PATHS
argument_list|,
name|configuration
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMountBelowUnsupportedPath
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"mnt"
argument_list|,
name|AbstractCugTest
operator|.
name|UNSUPPORTED_PATH
operator|+
literal|"/mount"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CugConfiguration
name|configuration
init|=
name|createConfiguration
argument_list|(
name|mip
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|AbstractCugTest
operator|.
name|SUPPORTED_PATHS
argument_list|,
name|configuration
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

