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
name|plugins
operator|.
name|index
operator|.
name|property
package|;
end_package

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
name|index
operator|.
name|property
operator|.
name|OrderedIndex
operator|.
name|OrderDirection
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
name|memory
operator|.
name|EmptyNodeState
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_comment
comment|/**  * tests the Enumeration for the index direction  */
end_comment

begin_class
specifier|public
class|class
name|OrderDirectionEnumTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|fromIndexMeta
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|OrderDirection
operator|.
name|fromIndexMeta
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|indexMeta
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|OrderDirection
operator|.
name|fromIndexMeta
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|ASC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|OrderDirection
operator|.
name|fromIndexMeta
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|DESC
argument_list|,
name|OrderDirection
operator|.
name|fromIndexMeta
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isDescending
parameter_list|()
block|{
name|NodeState
name|indexMeta
init|=
literal|null
decl_stmt|;
name|assertFalse
argument_list|(
name|OrderDirection
operator|.
name|isDescending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|ASC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|OrderDirection
operator|.
name|isDescending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|OrderDirection
operator|.
name|isDescending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isAscending
parameter_list|()
block|{
name|NodeState
name|indexMeta
init|=
literal|null
decl_stmt|;
name|assertTrue
argument_list|(
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|ASC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
name|indexMeta
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|OrderDirection
operator|.
name|DESC
operator|.
name|getDirection
argument_list|()
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|OrderDirection
operator|.
name|isAscending
argument_list|(
name|indexMeta
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderedDirectionFromString
parameter_list|()
block|{
name|assertNull
argument_list|(
literal|"A non-existing order direction should result in null"
argument_list|,
name|OrderDirection
operator|.
name|fromString
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|ASC
argument_list|,
name|OrderDirection
operator|.
name|fromString
argument_list|(
literal|"ascending"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|OrderDirection
operator|.
name|ASC
operator|.
name|equals
argument_list|(
name|OrderDirection
operator|.
name|fromString
argument_list|(
literal|"descending"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OrderDirection
operator|.
name|DESC
argument_list|,
name|OrderDirection
operator|.
name|fromString
argument_list|(
literal|"descending"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|OrderDirection
operator|.
name|DESC
operator|.
name|equals
argument_list|(
name|OrderDirection
operator|.
name|fromString
argument_list|(
literal|"ascending"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

