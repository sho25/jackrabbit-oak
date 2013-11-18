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
name|plugins
operator|.
name|observation
operator|.
name|filter
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
name|core
operator|.
name|ImmutableTree
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
name|observation
operator|.
name|filter
operator|.
name|EventGenerator
operator|.
name|Filter
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

begin_comment
comment|/**  * TODO PathFilter...  * TODO Clarify: filter applies to parent  */
end_comment

begin_class
specifier|public
class|class
name|PathFilter
implements|implements
name|Filter
block|{
specifier|private
specifier|final
name|ImmutableTree
name|afterTree
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
specifier|public
name|PathFilter
parameter_list|(
name|ImmutableTree
name|afterTree
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
name|this
operator|.
name|afterTree
operator|=
name|afterTree
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
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeChange
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeChange
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|,
name|NodeState
name|moved
parameter_list|)
block|{
return|return
name|includeByPath
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|includeChildren
argument_list|(
name|afterTree
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|PathFilter
argument_list|(
name|afterTree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|,
name|path
argument_list|,
name|deep
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|includeByPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|boolean
name|equalPaths
init|=
name|this
operator|.
name|path
operator|.
name|equals
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deep
operator|&&
operator|!
name|equalPaths
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
operator|(
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
operator|||
name|equalPaths
operator|)
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
comment|/**      * Determine whether the children of a {@code path} would be matched by this filter      * @param path  path whose children to test      * @return  {@code true} if the children of {@code path} could be matched by this filter      */
specifier|public
name|boolean
name|includeChildren
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|this
operator|.
name|path
argument_list|)
operator|||
name|path
operator|.
name|equals
argument_list|(
operator|(
name|this
operator|.
name|path
operator|)
argument_list|)
operator|||
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

