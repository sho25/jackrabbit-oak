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
name|observation
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_class
class|class
name|ChangeFilter
block|{
specifier|private
specifier|final
name|int
name|eventTypes
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|deep
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|uuid
decl_stmt|;
comment|// TODO implement filtering by uuid
specifier|private
specifier|final
name|String
index|[]
name|nodeTypeName
decl_stmt|;
comment|// TODO implement filtering by nodeTypeName
specifier|private
name|boolean
name|noLocal
decl_stmt|;
comment|// TODO implement filtering by noLocal
specifier|public
name|ChangeFilter
parameter_list|(
name|int
name|eventTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|String
index|[]
name|uuid
parameter_list|,
name|String
index|[]
name|nodeTypeName
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
block|{
name|this
operator|.
name|eventTypes
operator|=
name|eventTypes
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|deep
operator|=
name|deep
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
name|this
operator|.
name|nodeTypeName
operator|=
name|nodeTypeName
expr_stmt|;
name|this
operator|.
name|noLocal
operator|=
name|noLocal
expr_stmt|;
block|}
specifier|public
name|boolean
name|include
parameter_list|(
name|int
name|eventType
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|eventTypes
operator|&
name|eventType
operator|)
operator|!=
literal|0
return|;
block|}
specifier|public
name|boolean
name|include
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|deep
operator|&&
operator|!
name|this
operator|.
name|path
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|deep
operator|&&
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|include
parameter_list|(
name|int
name|eventType
parameter_list|,
name|String
name|path
parameter_list|,
name|NodeState
name|associatedParentNode
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|eventType
argument_list|)
operator|&&
name|include
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|includeChildren
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|deep
operator|&&
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

