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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
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
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|api
operator|.
name|JackrabbitRepository
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
name|NodeStore
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
name|test
operator|.
name|AbstractJCRTest
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
name|test
operator|.
name|NotExecutableException
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
name|SameNamePropertyNodeTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|String
name|sameName
init|=
literal|"sameName"
decl_stmt|;
specifier|private
name|Node
name|n
decl_stmt|;
specifier|private
name|Property
name|p
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|getDescriptorValue
argument_list|(
name|Repository
operator|.
name|OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED
argument_list|)
operator|.
name|getBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
literal|"node and property with same name is not supported"
argument_list|)
throw|;
block|}
name|n
operator|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|sameName
argument_list|)
expr_stmt|;
name|p
operator|=
name|testRootNode
operator|.
name|setProperty
argument_list|(
name|sameName
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSame
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
operator|.
name|isSame
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeExists
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionGetNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|nn
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasNode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|sameName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|testRootNode
operator|.
name|getNode
argument_list|(
name|sameName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyExists
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|superuser
operator|.
name|propertyExists
argument_list|(
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionGetProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Property
name|pp
init|=
name|superuser
operator|.
name|getProperty
argument_list|(
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|isSame
argument_list|(
name|pp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasProperty
argument_list|(
name|sameName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|p
operator|.
name|isSame
argument_list|(
name|testRootNode
operator|.
name|getProperty
argument_list|(
name|sameName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
operator|.
name|isSame
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testItemExists
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|superuser
operator|.
name|itemExists
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetItem
parameter_list|()
throws|throws
name|Exception
block|{
name|Item
name|item
init|=
name|superuser
operator|.
name|getItem
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|isNode
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|p
operator|.
name|isSame
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests if a microkernel fixture sets the SNNP repository descriptor to false.      */
annotation|@
name|Test
specifier|public
name|void
name|testMicroKernelSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|nodeStore
init|=
name|NodeStoreFixture
operator|.
name|DOCUMENT_MK
operator|.
name|createNodeStore
argument_list|()
decl_stmt|;
name|JackrabbitRepository
name|repository
init|=
operator|(
name|JackrabbitRepository
operator|)
operator|new
name|Jcr
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|repository
operator|.
name|getDescriptorValue
argument_list|(
name|Repository
operator|.
name|OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED
argument_list|)
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Tests if a nodestore fixture sets the SNNP repository descriptor to true.      */
annotation|@
name|Test
specifier|public
name|void
name|testNodeStoreSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|nodeStore
init|=
name|NodeStoreFixture
operator|.
name|SEGMENT_MK
operator|.
name|createNodeStore
argument_list|()
decl_stmt|;
name|JackrabbitRepository
name|repository
init|=
operator|(
name|JackrabbitRepository
operator|)
operator|new
name|Jcr
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|repository
operator|.
name|getDescriptorValue
argument_list|(
name|Repository
operator|.
name|OPTION_NODE_AND_PROPERTY_WITH_SAME_NAME_SUPPORTED
argument_list|)
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

