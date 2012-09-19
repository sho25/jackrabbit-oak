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
name|name
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
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
name|CommitFailedException
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
name|Root
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
name|core
operator|.
name|DefaultConflictHandler
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
name|StringValue
import|;
end_import

begin_comment
comment|/**  * Writable namespace registry. Mainly for use to implement the full JCR API.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadWriteNamespaceRegistry
extends|extends
name|ReadOnlyNamespaceRegistry
block|{
comment|/**      * Called by the write methods to acquire a fresh {@link Root} instance      * that can be used to persist the requested namespace changes (and      * nothing else).      *      * @return fresh {@link Root} instance      */
specifier|protected
specifier|abstract
name|Root
name|getWriteRoot
parameter_list|()
function_decl|;
comment|/**      * Called by the write methods to refresh the state of the possible      * session associated with this instance. The default implementation      * of this method does nothing, but a subclass can use this callback      * to keep a session in sync with the persisted namespace changes.      *      * @throws RepositoryException if the session could not be refreshed      */
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// do nothing
block|}
specifier|private
specifier|static
name|Tree
name|getOrCreate
parameter_list|(
name|Root
name|root
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
assert|assert
name|tree
operator|!=
literal|null
assert|;
for|for
control|(
name|String
name|name
range|:
name|path
control|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|child
operator|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|tree
operator|=
name|child
expr_stmt|;
block|}
return|return
name|tree
return|;
block|}
comment|//--------------------------------------------------< NamespaceRegistry>---
annotation|@
name|Override
specifier|public
name|void
name|registerNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|namespaces
init|=
name|getOrCreate
argument_list|(
name|root
argument_list|,
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|,
name|REP_NAMESPACES
argument_list|)
decl_stmt|;
comment|// remove existing mapping to given uri
for|for
control|(
name|PropertyState
name|p
range|:
name|namespaces
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|isArray
argument_list|()
operator|&&
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|namespaces
operator|.
name|removeProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|namespaces
operator|.
name|setProperty
argument_list|(
name|prefix
argument_list|,
operator|new
name|StringValue
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamespaceValidatorException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getNamespaceException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to register namespace mapping from "
operator|+
name|prefix
operator|+
literal|" to "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregisterNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|namespaces
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NAMESPACES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|==
literal|null
operator|||
operator|!
name|namespaces
operator|.
name|hasProperty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|(
literal|"Namespace mapping from "
operator|+
name|prefix
operator|+
literal|" to "
operator|+
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|+
literal|" can not be unregistered"
argument_list|)
throw|;
block|}
try|try
block|{
name|namespaces
operator|.
name|removeProperty
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamespaceValidatorException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getNamespaceException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to unregister namespace mapping for prefix "
operator|+
name|prefix
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

