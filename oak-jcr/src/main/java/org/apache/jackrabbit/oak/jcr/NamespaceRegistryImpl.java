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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

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
name|NamespaceRegistry
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
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_comment
comment|/**  * A naive implementation of {@link NamespaceRegistry}, hard-wiring the  * predefined namespaces for now.  */
end_comment

begin_class
specifier|public
class|class
name|NamespaceRegistryImpl
implements|implements
name|NamespaceRegistry
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
specifier|public
name|NamespaceRegistryImpl
parameter_list|()
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PREFIX_EMPTY
argument_list|,
name|NAMESPACE_EMPTY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PREFIX_JCR
argument_list|,
name|NAMESPACE_JCR
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PREFIX_MIX
argument_list|,
name|NAMESPACE_MIX
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PREFIX_NT
argument_list|,
name|NAMESPACE_NT
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PREFIX_XML
argument_list|,
name|NAMESPACE_XML
argument_list|)
expr_stmt|;
block|}
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
name|NamespaceException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|AccessDeniedException
throws|,
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|NamespaceException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|AccessDeniedException
throws|,
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getPrefixes
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getURIs
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|map
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getURI
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|NamespaceException
throws|,
name|RepositoryException
block|{
name|String
name|result
init|=
name|map
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NamespaceException
argument_list|()
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrefix
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|NamespaceException
throws|,
name|RepositoryException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|NamespaceException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

