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
name|security
operator|.
name|authorization
operator|.
name|evaluation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|JcrConstants
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|api
operator|.
name|Type
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
name|MoveDetector
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertNull
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
comment|/**  * Test for the hidden {@link org.apache.jackrabbit.oak.plugins.tree.TreeConstants#OAK_CHILD_ORDER} property  */
end_comment

begin_class
specifier|public
class|class
name|HiddenPropertyTest
extends|extends
name|AbstractOakCoreTest
block|{
specifier|private
name|String
index|[]
name|hiddenProps
init|=
operator|new
name|String
index|[]
block|{
literal|":hiddenProp"
block|,
name|MoveDetector
operator|.
name|SOURCE_PATH
block|,
name|VersionConstants
operator|.
name|HIDDEN_COPY_SOURCE
block|}
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|":hiddenProp"
argument_list|,
literal|"val"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
name|MoveDetector
operator|.
name|SOURCE_PATH
argument_list|,
literal|"/some/path"
argument_list|,
name|Type
operator|.
name|PATH
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
name|VersionConstants
operator|.
name|HIDDEN_COPY_SOURCE
argument_list|,
literal|"abc"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasProperty
parameter_list|()
block|{
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|hiddenProps
control|)
block|{
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperty
parameter_list|()
block|{
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|hiddenProps
control|)
block|{
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperties
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
literal|"aProp"
argument_list|)
decl_stmt|;
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|prop
range|:
name|a
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|propertyNames
operator|.
name|remove
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|propertyNames
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPropertyCount
parameter_list|()
block|{
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|a
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPropertyStatus
parameter_list|()
block|{
name|Tree
name|a
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|hiddenProps
control|)
block|{
name|assertNull
argument_list|(
name|a
operator|.
name|getPropertyStatus
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

