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
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonNode
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|smile
operator|.
name|SmileFactory
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
name|ContentRepository
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
name|ContentSession
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
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import

begin_class
specifier|public
class|class
name|OakServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|MediaType
name|JSON
init|=
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/json"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|SMILE
init|=
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/x-jackson-smile"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Representation
index|[]
name|REPRESENTATIONS
init|=
block|{
operator|new
name|JsonRepresentation
argument_list|(
name|JSON
argument_list|,
operator|new
name|JsonFactory
argument_list|()
argument_list|)
block|,
operator|new
name|JsonRepresentation
argument_list|(
name|SMILE
argument_list|,
operator|new
name|SmileFactory
argument_list|()
argument_list|)
block|,
operator|new
name|PostRepresentation
argument_list|()
block|,
operator|new
name|TextRepresentation
argument_list|()
block|}
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|repository
decl_stmt|;
specifier|public
name|OakServlet
parameter_list|(
name|ContentRepository
name|repository
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|ContentSession
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
literal|"root"
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|// Find the longest part of the given path that matches
comment|// an existing node. The tail part might be used when
comment|// creating new nodes or when exposing virtual resources.
comment|// Note that we need to traverse the path in reverse
comment|// direction as some parent nodes may be read-protected.
name|String
name|head
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
name|String
name|tail
init|=
literal|""
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|head
argument_list|)
decl_stmt|;
while|while
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
name|int
name|slash
init|=
name|head
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|tail
operator|=
name|head
operator|.
name|substring
argument_list|(
name|slash
argument_list|)
operator|+
name|tail
expr_stmt|;
name|head
operator|=
name|head
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
operator|-
literal|1
argument_list|)
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
name|tail
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|setAttribute
argument_list|(
literal|"tree"
argument_list|,
name|tree
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
literal|"path"
argument_list|,
name|tail
argument_list|)
expr_stmt|;
name|super
operator|.
name|service
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchWorkspaceException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|AcceptHeader
name|accept
init|=
operator|new
name|AcceptHeader
argument_list|(
name|request
operator|.
name|getHeader
argument_list|(
literal|"Accept"
argument_list|)
argument_list|)
decl_stmt|;
name|Representation
name|representation
init|=
name|accept
operator|.
name|resolve
argument_list|(
name|REPRESENTATIONS
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Tree
name|tree
init|=
operator|(
name|Tree
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"tree"
argument_list|)
decl_stmt|;
name|representation
operator|.
name|render
argument_list|(
name|tree
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// There was an extra path component that didn't match
comment|// any existing nodes, so for now we just send a 404 response.
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|Root
name|root
init|=
operator|(
name|Root
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
operator|(
name|Tree
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"tree"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
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
name|path
argument_list|)
control|)
block|{
name|tree
operator|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|JsonNode
name|node
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isObject
argument_list|()
condition|)
block|{
name|post
argument_list|(
name|node
argument_list|,
name|tree
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|post
parameter_list|(
name|JsonNode
name|node
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonNode
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonNode
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|JsonNode
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|isObject
argument_list|()
condition|)
block|{
if|if
condition|(
name|tree
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tree
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
name|post
argument_list|(
name|value
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
else|else
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
operator|!=
literal|null
condition|)
block|{
name|child
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|isNull
argument_list|()
condition|)
block|{
name|tree
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|isBoolean
argument_list|()
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|asBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|isLong
argument_list|()
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|asLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|isDouble
argument_list|()
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|asDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|isBigDecimal
argument_list|()
condition|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|decimalValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|asText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doDelete
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|Root
name|root
init|=
operator|(
name|Root
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
operator|(
name|Tree
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"tree"
argument_list|)
decl_stmt|;
name|Tree
name|parent
init|=
name|tree
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|Tree
name|child
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|child
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Can't remove the root node
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

