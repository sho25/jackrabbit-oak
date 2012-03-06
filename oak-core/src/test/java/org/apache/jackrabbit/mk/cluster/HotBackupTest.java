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
name|mk
operator|.
name|cluster
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
name|mk
operator|.
name|MicroKernelFactory
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|HotBackupTest
block|{
specifier|private
name|MicroKernel
name|source
decl_stmt|;
specifier|private
name|MicroKernel
name|target
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|source
operator|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
literal|"fs:{homeDir}/target/mk1;clean"
argument_list|)
expr_stmt|;
name|target
operator|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
literal|"fs:{homeDir}/target/mk2;clean"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|source
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
name|target
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|HotBackup
name|hotbackup
init|=
operator|new
name|HotBackup
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|source
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\":{}"
argument_list|,
name|source
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|hotbackup
operator|.
name|sync
argument_list|()
expr_stmt|;
name|target
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|target
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

