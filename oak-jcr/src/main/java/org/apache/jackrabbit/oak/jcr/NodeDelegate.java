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
name|api
operator|.
name|Tree
operator|.
name|Status
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
name|util
operator|.
name|Function1
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
name|util
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|NodeDelegate
extends|extends
name|ItemDelegate
block|{
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
name|Tree
name|tree
decl_stmt|;
name|NodeDelegate
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
block|}
name|NodeDelegate
name|addNode
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|parentState
init|=
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|relPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|relPath
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|parentState
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionContext
argument_list|,
name|parentState
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
name|NodeDelegate
name|getAncestor
parameter_list|(
name|int
name|depth
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|current
init|=
name|getDepth
argument_list|()
decl_stmt|;
if|if
condition|(
name|depth
argument_list|<
literal|0
operator|||
name|depth
argument_list|>
name|current
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
literal|"ancestor at depth "
operator|+
name|depth
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|Tree
name|ancestor
init|=
name|getTree
argument_list|()
decl_stmt|;
while|while
condition|(
name|depth
operator|<
name|current
condition|)
block|{
name|ancestor
operator|=
name|ancestor
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|current
operator|-=
literal|1
expr_stmt|;
block|}
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionContext
argument_list|,
name|ancestor
argument_list|)
return|;
block|}
name|Iterator
argument_list|<
name|NodeDelegate
argument_list|>
name|getChildren
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|nodeDelegateIterator
argument_list|(
name|getTree
argument_list|()
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
name|long
name|getChildrenCount
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getChildrenCount
argument_list|()
return|;
block|}
annotation|@
name|Override
name|String
name|getName
parameter_list|()
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
name|Status
name|getNodeStatus
parameter_list|()
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|getChildStatus
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
name|NodeDelegate
name|getNodeOrNull
parameter_list|(
name|String
name|relOakPath
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|relOakPath
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|sessionContext
argument_list|,
name|tree
argument_list|)
return|;
block|}
name|NodeDelegate
name|getParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|check
argument_list|(
name|getTree
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
literal|"Root has no parent"
argument_list|)
throw|;
block|}
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionContext
argument_list|,
name|getTree
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
name|String
name|getPath
parameter_list|()
block|{
return|return
literal|'/'
operator|+
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
name|Iterator
argument_list|<
name|PropertyDelegate
argument_list|>
name|getProperties
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|propertyDelegateIterator
argument_list|(
name|getTree
argument_list|()
operator|.
name|getProperties
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
name|long
name|getPropertyCount
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
name|PropertyDelegate
name|getPropertyOrNull
parameter_list|(
name|String
name|relOakPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|parent
init|=
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|relOakPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|relOakPath
argument_list|)
decl_stmt|;
name|PropertyState
name|propertyState
init|=
name|parent
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|propertyState
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|PropertyDelegate
argument_list|(
name|sessionContext
argument_list|,
name|parent
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
name|SessionContext
name|getSessionContext
parameter_list|()
block|{
return|return
name|sessionContext
return|;
block|}
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|getTree
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|removeChild
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyDelegate
name|setProperty
parameter_list|(
name|String
name|oakName
parameter_list|,
name|CoreValue
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|getTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|oakName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|getPropertyOrNull
argument_list|(
name|oakName
argument_list|)
return|;
block|}
name|PropertyDelegate
name|setProperty
parameter_list|(
name|String
name|oakName
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|getTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|oakName
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|getPropertyOrNull
argument_list|(
name|oakName
argument_list|)
return|;
block|}
comment|// -----------------------------------------------------------< private>---
specifier|private
name|Tree
name|getTree
parameter_list|(
name|String
name|relPath
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|relPath
argument_list|)
control|)
block|{
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|tree
return|;
block|}
specifier|private
specifier|static
name|Tree
name|check
parameter_list|(
name|Tree
name|t
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|()
throw|;
block|}
return|return
name|t
return|;
block|}
specifier|private
specifier|synchronized
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|tree
operator|=
name|sessionContext
operator|.
name|getTree
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|NodeDelegate
argument_list|>
name|nodeDelegateIterator
parameter_list|(
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|childNodeStates
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|childNodeStates
argument_list|,
operator|new
name|Function1
argument_list|<
name|Tree
argument_list|,
name|NodeDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDelegate
name|apply
parameter_list|(
name|Tree
name|state
parameter_list|)
block|{
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionContext
argument_list|,
name|state
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|PropertyDelegate
argument_list|>
name|propertyDelegateIterator
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|properties
argument_list|,
operator|new
name|Function1
argument_list|<
name|PropertyState
argument_list|,
name|PropertyDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PropertyDelegate
name|apply
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
operator|new
name|PropertyDelegate
argument_list|(
name|sessionContext
argument_list|,
name|tree
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

