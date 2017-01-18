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
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|standby
operator|.
name|client
operator|.
name|StandbyClientSync
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

begin_class
specifier|public
class|class
name|TestBase
block|{
specifier|private
specifier|static
specifier|final
name|int
name|timeout
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"standby.test.timeout"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
comment|// Java 6 on Windows doesn't support dual IP stacks, so we will skip our
comment|// IPv6 tests.
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
operator|!
name|CIHelper
operator|.
name|travis
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|getServerHost
parameter_list|()
block|{
return|return
literal|"127.0.0.1"
return|;
block|}
specifier|static
name|int
name|getClientTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
specifier|public
name|StandbyClientSync
name|newStandbyClientSync
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|newStandbyClientSync
argument_list|(
name|store
argument_list|,
name|port
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|StandbyClientSync
name|newStandbyClientSync
parameter_list|(
name|FileStore
name|store
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|secure
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|StandbyClientSync
argument_list|(
name|getServerHost
argument_list|()
argument_list|,
name|port
argument_list|,
name|store
argument_list|,
name|secure
argument_list|,
name|getClientTimeout
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

