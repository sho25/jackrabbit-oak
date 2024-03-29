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
name|jcr
operator|.
name|version
package|;
end_package

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
name|NodeStoreFixture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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

begin_comment
comment|/**  * Test versionable paths with multiple node stores.  * See OAK-3169 for details.  */
end_comment

begin_class
specifier|public
class|class
name|VersionablePathNodeStoreTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|private
name|Session
name|session
decl_stmt|;
specifier|public
name|VersionablePathNodeStoreTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|=
name|getAdminSession
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVersionablePaths
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|char
name|i
init|=
literal|'a'
init|;
name|i
operator|<=
literal|'z'
condition|;
name|i
operator|++
control|)
block|{
name|versionablePaths
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|versionablePaths
parameter_list|(
name|String
name|nodeName
parameter_list|)
throws|throws
name|Exception
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
literal|"jcr:versionHistory"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|Node
name|n2
init|=
name|session
operator|.
name|getNodeByIdentifier
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"nodeName "
operator|+
name|nodeName
argument_list|,
name|n2
operator|.
name|isNodeType
argument_list|(
literal|"rep:VersionablePaths"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

