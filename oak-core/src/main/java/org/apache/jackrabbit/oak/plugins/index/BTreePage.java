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

begin_comment
comment|/**  * An index page.  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|BTreePage
implements|implements
name|PropertyIndexConstants
block|{
specifier|protected
specifier|final
name|BTree
name|tree
decl_stmt|;
specifier|protected
name|BTreeNode
name|parent
decl_stmt|;
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|String
index|[]
name|keys
decl_stmt|;
specifier|protected
name|String
index|[]
name|values
decl_stmt|;
name|BTreePage
parameter_list|(
name|BTree
name|tree
parameter_list|,
name|BTreeNode
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
index|[]
name|keys
parameter_list|,
name|String
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|abstract
name|void
name|writeCreate
parameter_list|()
function_decl|;
specifier|abstract
name|void
name|split
parameter_list|(
name|BTreeNode
name|newParent
parameter_list|,
name|String
name|newPath
parameter_list|,
name|int
name|pos
parameter_list|,
name|String
name|siblingPath
parameter_list|)
function_decl|;
specifier|abstract
name|BTreeLeaf
name|firstLeaf
parameter_list|()
function_decl|;
name|void
name|setParent
parameter_list|(
name|BTreeNode
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|,
name|boolean
name|parentIsNew
parameter_list|)
block|{
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
name|String
name|oldPath
init|=
name|getPath
argument_list|()
decl_stmt|;
name|String
name|temp
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|INDEX_CONTENT
argument_list|,
literal|"temp"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|bufferMove
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|INDEX_CONTENT
argument_list|,
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|temp
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentIsNew
condition|)
block|{
name|newParent
operator|.
name|writeCreate
argument_list|()
expr_stmt|;
block|}
name|tree
operator|.
name|bufferMove
argument_list|(
name|temp
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|INDEX_CONTENT
argument_list|,
name|getParentPath
argument_list|()
argument_list|,
name|newName
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|=
name|newParent
expr_stmt|;
name|name
operator|=
name|newName
expr_stmt|;
name|tree
operator|.
name|moveCache
argument_list|(
name|oldPath
argument_list|)
expr_stmt|;
name|tree
operator|.
name|modified
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|getParentPath
parameter_list|()
block|{
return|return
name|parent
operator|==
literal|null
condition|?
literal|""
else|:
name|parent
operator|.
name|getPath
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|getParentPath
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|keys
operator|.
name|length
return|;
block|}
name|int
name|find
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|tree
operator|.
name|find
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|keys
argument_list|,
name|values
argument_list|)
return|;
block|}
block|}
end_class

end_unit

