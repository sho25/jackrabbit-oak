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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|document
operator|.
name|util
operator|.
name|Utils
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
comment|/**  *<code>UpdateUtilsTest</code>...  */
end_comment

begin_class
specifier|public
class|class
name|UpdateUtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|applyChanges
parameter_list|()
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|put
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42L
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|max
argument_list|(
literal|"p"
argument_list|,
literal|23L
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42L
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|max
argument_list|(
literal|"p"
argument_list|,
literal|58L
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|58L
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|increment
argument_list|(
literal|"p"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|61L
argument_list|,
name|d
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|getMapEntry
argument_list|(
name|d
argument_list|,
literal|"t"
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|removeMapEntry
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getMapEntry
argument_list|(
name|d
argument_list|,
literal|"t"
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkConditions
parameter_list|()
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|put
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
literal|"p"
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|d
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|containsMapEntry
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|containsMapEntry
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|containsMapEntry
argument_list|(
literal|"q"
argument_list|,
name|r
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|containsMapEntry
argument_list|(
literal|"q"
argument_list|,
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"t"
argument_list|,
name|r
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"t"
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"t"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|,
name|r
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"p"
argument_list|,
name|r
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"p"
argument_list|,
literal|42L
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|,
literal|7L
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"p"
argument_list|,
literal|7L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// check on non-existing property
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"other"
argument_list|,
literal|7L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"other"
argument_list|,
name|r
argument_list|,
literal|7L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"other"
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|equals
argument_list|(
literal|"other"
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// check null
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"p"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|op
operator|=
name|newUpdateOp
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|notEquals
argument_list|(
literal|"other"
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|d
argument_list|,
name|op
operator|.
name|getConditions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|UpdateOp
name|newUpdateOp
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Object
name|getMapEntry
parameter_list|(
name|Document
name|d
parameter_list|,
name|String
name|name
parameter_list|,
name|Revision
name|r
parameter_list|)
block|{
name|Object
name|obj
init|=
name|d
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
operator|(
name|Map
operator|)
name|obj
operator|)
operator|.
name|get
argument_list|(
name|r
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

