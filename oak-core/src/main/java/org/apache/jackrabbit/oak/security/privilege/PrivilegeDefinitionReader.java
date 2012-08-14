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
name|security
operator|.
name|privilege
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeDefinition
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
name|NodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_AGGREGATES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_IS_ABSTRACT
import|;
end_import

begin_comment
comment|/**  * Reads privilege definitions without applying any validation.  */
end_comment

begin_class
class|class
name|PrivilegeDefinitionReader
block|{
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
name|PrivilegeDefinitionReader
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|readDefinitions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
argument_list|()
decl_stmt|;
name|Tree
name|privilegesTree
init|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|privilegesTree
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Tree
name|child
range|:
name|privilegesTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|PrivilegeDefinition
name|def
init|=
name|readDefinition
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|definitions
return|;
block|}
name|PrivilegeDefinition
name|readDefinition
parameter_list|(
name|Tree
name|definitionTree
parameter_list|)
block|{
name|NodeUtil
name|n
init|=
operator|new
name|NodeUtil
argument_list|(
name|definitionTree
argument_list|,
name|contentSession
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|n
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|isAbstract
init|=
name|n
operator|.
name|getBoolean
argument_list|(
name|REP_IS_ABSTRACT
argument_list|)
decl_stmt|;
name|String
index|[]
name|declAggrNames
init|=
name|n
operator|.
name|getStrings
argument_list|(
name|REP_AGGREGATES
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|declAggrNames
argument_list|)
return|;
block|}
comment|/**      * Reads privilege definitions for the specified {@code InputStream}. The      * aim of this method is to provide backwards compatibility with      * custom privilege definitions of Jackrabbit 2.x repositories. The caller      * is in charge of migrating the definitions.      *      * @param customPrivileges      * @param nsRegistry      * @return      * @throws RepositoryException      * @throws IOException      */
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|readCustomDefinitons
parameter_list|(
name|InputStream
name|customPrivileges
parameter_list|,
name|NamespaceRegistry
name|nsRegistry
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
argument_list|()
decl_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|customPrivileges
argument_list|)
decl_stmt|;
for|for
control|(
name|PrivilegeDefinition
name|def
range|:
name|PrivilegeXmlHandler
operator|.
name|readDefinitions
argument_list|(
name|src
argument_list|,
name|nsRegistry
argument_list|)
control|)
block|{
name|String
name|privName
init|=
name|def
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|definitions
operator|.
name|containsKey
argument_list|(
name|privName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Duplicate entry for custom privilege with name "
operator|+
name|privName
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|definitions
operator|.
name|put
argument_list|(
name|privName
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
return|return
name|definitions
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * The {@code PrivilegeXmlHandler} loads privilege definitions from a XML      * document using the following format:      *<pre>      *&lt;!DOCTYPE privileges [      *&lt;!ELEMENT privileges (privilege)+&gt;      *&lt;!ELEMENT privilege (contains)+&gt;      *&lt;!ATTLIST privilege abstract (true|false) false&gt;      *&lt;!ATTLIST privilege name NMTOKEN #REQUIRED&gt;      *&lt;!ELEMENT contains EMPTY&gt;      *&lt;!ATTLIST contains name NMTOKEN #REQUIRED&gt;      * ]>      *</pre>      */
specifier|private
specifier|static
class|class
name|PrivilegeXmlHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEXT_XML
init|=
literal|"text/xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_XML
init|=
literal|"application/xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_PRIVILEGES
init|=
literal|"privileges"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_PRIVILEGE
init|=
literal|"privilege"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_CONTAINS
init|=
literal|"contains"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTR_NAME
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTR_ABSTRACT
init|=
literal|"abstract"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTR_XMLNS
init|=
literal|"xmlns:"
decl_stmt|;
specifier|private
specifier|static
name|DocumentBuilderFactory
name|DOCUMENT_BUILDER_FACTORY
init|=
name|createFactory
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|DocumentBuilderFactory
name|createFactory
parameter_list|()
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIgnoringComments
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setIgnoringElementContentWhitespace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|private
specifier|static
name|PrivilegeDefinition
index|[]
name|readDefinitions
parameter_list|(
name|InputSource
name|input
parameter_list|,
name|NamespaceRegistry
name|nsRegistry
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
try|try
block|{
name|List
argument_list|<
name|PrivilegeDefinition
argument_list|>
name|defs
init|=
operator|new
name|ArrayList
argument_list|<
name|PrivilegeDefinition
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|createDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|XML_PRIVILEGES
operator|.
name|equals
argument_list|(
name|root
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"root element must be named 'privileges'"
argument_list|)
throw|;
block|}
name|updateNamespaceMapping
argument_list|(
name|root
argument_list|,
name|nsRegistry
argument_list|)
expr_stmt|;
name|NodeList
name|nl
init|=
name|root
operator|.
name|getElementsByTagName
argument_list|(
name|XML_PRIVILEGE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|PrivilegeDefinition
name|def
init|=
name|parseDefinition
argument_list|(
name|n
argument_list|,
name|nsRegistry
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
condition|)
block|{
name|defs
operator|.
name|add
argument_list|(
name|def
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|defs
operator|.
name|toArray
argument_list|(
operator|new
name|PrivilegeDefinition
index|[
name|defs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**          * Build a new {@code PrivilegeDefinition} from the given XML node.          * @param n the xml node storing the privilege definition.          * @param nsRegistry          * @return a new PrivilegeDefinition.          * @throws javax.jcr.RepositoryException          */
specifier|private
specifier|static
name|PrivilegeDefinition
name|parseDefinition
parameter_list|(
name|Node
name|n
parameter_list|,
name|NamespaceRegistry
name|nsRegistry
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|n
decl_stmt|;
name|updateNamespaceMapping
argument_list|(
name|elem
argument_list|,
name|nsRegistry
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|ATTR_NAME
argument_list|)
decl_stmt|;
name|boolean
name|isAbstract
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
name|ATTR_ABSTRACT
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|aggrNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|nodeList
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|contains
init|=
name|nodeList
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|isElement
argument_list|(
name|n
argument_list|)
operator|&&
name|XML_CONTAINS
operator|.
name|equals
argument_list|(
name|contains
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|aggrName
init|=
operator|(
operator|(
name|Element
operator|)
name|contains
operator|)
operator|.
name|getAttribute
argument_list|(
name|ATTR_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggrName
operator|!=
literal|null
condition|)
block|{
name|aggrNames
operator|.
name|add
argument_list|(
name|aggrName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
return|;
block|}
comment|// could not parse into privilege definition
return|return
literal|null
return|;
block|}
comment|/**          * Create a new {@code DocumentBuilder}          *          * @return a new {@code DocumentBuilder}          * @throws ParserConfigurationException          */
specifier|private
specifier|static
name|DocumentBuilder
name|createDocumentBuilder
parameter_list|()
throws|throws
name|ParserConfigurationException
block|{
name|DocumentBuilder
name|builder
init|=
name|DOCUMENT_BUILDER_FACTORY
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setErrorHandler
argument_list|(
operator|new
name|DefaultHandler
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**          * Update the specified nsRegistry mappings with the nsRegistry declarations          * defined by the given XML element.          *          * @param elem          * @param nsRegistry          * @throws javax.jcr.RepositoryException          */
specifier|private
specifier|static
name|void
name|updateNamespaceMapping
parameter_list|(
name|Element
name|elem
parameter_list|,
name|NamespaceRegistry
name|nsRegistry
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NamedNodeMap
name|attributes
init|=
name|elem
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Attr
name|attr
init|=
operator|(
name|Attr
operator|)
name|attributes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|ATTR_XMLNS
argument_list|)
condition|)
block|{
name|String
name|prefix
init|=
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|ATTR_XMLNS
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|attr
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|nsRegistry
operator|.
name|registerNamespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**          * Returns {@code true} if the given XML node is an element.          *          * @param n          * @return {@code true} if the given XML node is an element; {@code false} otherwise.          */
specifier|private
specifier|static
name|boolean
name|isElement
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
return|return
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
return|;
block|}
block|}
block|}
end_class

end_unit

