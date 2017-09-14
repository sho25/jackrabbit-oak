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
name|plugins
operator|.
name|version
operator|.
name|VersionConstants
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|BUNDLOR
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|DOCUMENT_NODE_STORE
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|assertFalse
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
comment|/**  * Test for OAK-2459.  */
end_comment

begin_class
specifier|public
class|class
name|InitialContentTest
implements|implements
name|VersionConstants
block|{
annotation|@
name|Test
specifier|public
name|void
name|noVersionStoragePrePopulated
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default initial content does not have intermediate nodes
comment|// pre-populated
name|NodeState
name|system
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|system
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|vs
init|=
name|system
operator|.
name|getChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vs
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vs
operator|.
name|getChildNodeCount
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|versionStoragePrePopulated
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|withPrePopulatedVersionStore
argument_list|()
operator|.
name|initialize
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|NodeBuilder
name|system
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|system
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|vs
init|=
name|system
operator|.
name|getChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vs
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// check if two levels of intermediate nodes were created
name|assertTrue
argument_list|(
name|vs
operator|.
name|getChildNodeCount
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|==
literal|0xff
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|vs
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|vs
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|getChildNodeCount
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|==
literal|0xff
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|bundlingConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|system
init|=
name|InitialContent
operator|.
name|INITIAL_CONTENT
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|system
operator|.
name|getChildNode
argument_list|(
name|DOCUMENT_NODE_STORE
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

