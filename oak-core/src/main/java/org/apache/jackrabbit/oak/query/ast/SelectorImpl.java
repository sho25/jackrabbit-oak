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
name|query
operator|.
name|ast
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
name|api
operator|.
name|MicroKernel
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
name|CoreValue
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
name|plugins
operator|.
name|memory
operator|.
name|SinglePropertyState
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
name|query
operator|.
name|Query
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|Cursor
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
name|QueryIndex
import|;
end_import

begin_comment
comment|/**  * A selector within a query.  */
end_comment

begin_class
specifier|public
class|class
name|SelectorImpl
extends|extends
name|SourceImpl
block|{
comment|// TODO jcr:path isn't an official feature, support it?
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|"jcr:path"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JCR_PRIMARY_TYPE
init|=
literal|"jcr:primaryType"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_BASE
init|=
literal|"nt:base"
decl_stmt|;
comment|// TODO possibly support using multiple indexes (using index intersection / index merge)
specifier|protected
name|QueryIndex
name|index
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeTypeName
decl_stmt|,
name|selectorName
decl_stmt|;
specifier|private
name|Cursor
name|cursor
decl_stmt|;
specifier|public
name|SelectorImpl
parameter_list|(
name|String
name|nodeTypeName
parameter_list|,
name|String
name|selectorName
parameter_list|)
block|{
name|this
operator|.
name|nodeTypeName
operator|=
name|nodeTypeName
expr_stmt|;
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
block|}
specifier|public
name|String
name|getNodeTypeName
parameter_list|()
block|{
return|return
name|nodeTypeName
return|;
block|}
specifier|public
name|String
name|getSelectorName
parameter_list|()
block|{
return|return
name|selectorName
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// TODO quote nodeTypeName?
return|return
name|nodeTypeName
operator|+
literal|" as "
operator|+
name|getSelectorName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|index
operator|=
name|query
operator|.
name|getBestIndex
argument_list|(
name|createFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
name|revisionId
parameter_list|)
block|{
name|cursor
operator|=
name|index
operator|.
name|query
argument_list|(
name|createFilter
argument_list|()
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|()
block|{
return|return
name|nodeTypeName
operator|+
literal|" as "
operator|+
name|getSelectorName
argument_list|()
operator|+
literal|" /* "
operator|+
name|index
operator|.
name|getPlan
argument_list|(
name|createFilter
argument_list|()
argument_list|)
operator|+
literal|" */"
return|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|()
block|{
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|f
operator|.
name|setNodeType
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
if|if
condition|(
name|joinCondition
operator|!=
literal|null
condition|)
block|{
name|joinCondition
operator|.
name|apply
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|outerJoin
condition|)
block|{
comment|// for outer joins, query constraints can't be applied to the
comment|// filter, because that would alter the result
if|if
condition|(
name|queryConstraint
operator|!=
literal|null
condition|)
block|{
name|queryConstraint
operator|.
name|apply
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|cursor
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|result
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|TYPE_BASE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|cursor
operator|.
name|currentPath
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|p
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARY_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|CoreValue
name|v
init|=
name|p
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// TODO node type matching
if|if
condition|(
name|nodeTypeName
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|currentPath
parameter_list|()
block|{
return|return
name|cursor
operator|==
literal|null
condition|?
literal|null
else|:
name|cursor
operator|.
name|currentPath
argument_list|()
return|;
block|}
specifier|public
name|PropertyState
name|currentProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
name|propertyName
operator|.
name|equals
argument_list|(
name|PATH
argument_list|)
condition|)
block|{
name|String
name|p
init|=
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|local
init|=
name|getLocalPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|local
operator|==
literal|null
condition|)
block|{
comment|// not a local path
return|return
literal|null
return|;
block|}
name|CoreValue
name|v
init|=
name|query
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|local
argument_list|)
decl_stmt|;
return|return
operator|new
name|SinglePropertyState
argument_list|(
name|PATH
argument_list|,
name|v
argument_list|)
return|;
block|}
name|String
name|path
init|=
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|SelectorImpl
name|getSelector
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
if|if
condition|(
name|selectorName
operator|.
name|equals
argument_list|(
name|this
operator|.
name|selectorName
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

