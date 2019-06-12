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
name|api
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
name|io
operator|.
name|InputStream
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
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * The Jackrabbit node type manager interface. This interface contains the  * Jackrabbit-specific extensions to the JCR {@link NodeTypeManager} interface.  *<p>  * Currently Jackrabbit provides a mechanism to register new node types, but  * it is not possible to modify or remove existing node types.  *  * @deprecated Use standard JCR 2.0 API methods defined by  * {@link NodeTypeManager} instead.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JackrabbitNodeTypeManager
extends|extends
name|NodeTypeManager
block|{
comment|/**      * The standard XML content type to be used with XML-formatted      * node type streams.      */
name|String
name|TEXT_XML
init|=
literal|"text/xml"
decl_stmt|;
comment|/**      * The experimental content type for the compact node type definition      * files.      */
name|String
name|TEXT_X_JCR_CND
init|=
literal|"text/x-jcr-cnd"
decl_stmt|;
comment|/**      * Registers node types from the given node type XML stream.      *      * @param in node type XML stream      * @return registered node types      * @throws SAXException if the XML stream could not be read or parsed      * @throws RepositoryException if the node types are invalid or another      *                             repository error occurs      */
name|NodeType
index|[]
name|registerNodeTypes
parameter_list|(
name|InputSource
name|in
parameter_list|)
throws|throws
name|SAXException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Registers node types from the given input stream of the given type.      *      * @param in node type stream      * @param contentType type of the input stream      * @return registered node types      * @throws IOException if the input stream could not be read or parsed      * @throws RepositoryException if the node types are invalid or another      *                             repository error occurs      */
name|NodeType
index|[]
name|registerNodeTypes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|contentType
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Checks if a node type with the given name is registered.      *      * @param name node type name      * @return<code>true</code> if the named node type is registered      *<code>false</code> otherwise      * @throws RepositoryException if an error occurs      */
name|boolean
name|hasNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

