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
name|xml
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
name|ArrayList
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
name|Stack
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidSerializedDataException
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
name|PropertyType
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
name|oak
operator|.
name|jcr
operator|.
name|session
operator|.
name|SessionContext
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
name|namespace
operator|.
name|NamespaceConstants
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
name|xml
operator|.
name|Importer
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
name|xml
operator|.
name|NodeInfo
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
name|xml
operator|.
name|PropInfo
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
name|Attributes
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
comment|/**  * {@code SysViewImportHandler}  ...  */
end_comment

begin_class
class|class
name|SysViewImportHandler
extends|extends
name|TargetImportHandler
block|{
comment|/**      * stack of ImportState instances; an instance is pushed onto the stack      * in the startElement method every time a sv:node element is encountered;      * the same instance is popped from the stack in the endElement method      * when the corresponding sv:node element is encountered.      */
specifier|private
specifier|final
name|Stack
argument_list|<
name|ImportState
argument_list|>
name|stack
init|=
operator|new
name|Stack
argument_list|<
name|ImportState
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|BufferedStringValue
argument_list|>
name|currentPropValues
init|=
operator|new
name|ArrayList
argument_list|<
name|BufferedStringValue
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * fields used temporarily while processing sv:property and sv:value elements      */
specifier|private
name|NameInfo
name|currentPropName
decl_stmt|;
specifier|private
name|int
name|currentPropType
init|=
name|PropertyType
operator|.
name|UNDEFINED
decl_stmt|;
specifier|private
name|PropInfo
operator|.
name|MultipleStatus
name|currentPropMultipleStatus
init|=
name|PropInfo
operator|.
name|MultipleStatus
operator|.
name|UNKNOWN
decl_stmt|;
comment|// list of appendable value objects
specifier|private
name|BufferedStringValue
name|currentPropValue
decl_stmt|;
comment|/**      * Constructs a new {@code SysViewImportHandler}.      *      * @param importer     the underlying importer      * @param sessionContext the session context      */
name|SysViewImportHandler
parameter_list|(
name|Importer
name|importer
parameter_list|,
name|SessionContext
name|sessionContext
parameter_list|)
block|{
name|super
argument_list|(
name|importer
argument_list|,
name|sessionContext
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|processNode
parameter_list|(
name|ImportState
name|state
parameter_list|,
name|boolean
name|start
parameter_list|,
name|boolean
name|end
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|start
operator|&&
operator|!
name|end
condition|)
block|{
return|return;
block|}
name|String
name|id
init|=
name|state
operator|.
name|uuid
decl_stmt|;
name|NodeInfo
name|node
init|=
operator|new
name|NodeInfo
argument_list|(
name|state
operator|.
name|nodeName
argument_list|,
name|state
operator|.
name|nodeTypeName
argument_list|,
name|state
operator|.
name|mixinNames
argument_list|,
name|id
argument_list|)
decl_stmt|;
comment|// call Importer
try|try
block|{
if|if
condition|(
name|start
condition|)
block|{
name|importer
operator|.
name|startNode
argument_list|(
name|node
argument_list|,
name|state
operator|.
name|props
argument_list|)
expr_stmt|;
comment|// dispose temporary property values
for|for
control|(
name|PropInfo
name|pi
range|:
name|state
operator|.
name|props
control|)
block|{
name|pi
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|end
condition|)
block|{
name|importer
operator|.
name|endNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|re
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|re
argument_list|)
throw|;
block|}
block|}
comment|//-------------------------------------------------------< ContentHandler>
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// check element name
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"node"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:node element
comment|// node name (value of sv:name attribute)
name|String
name|svName
init|=
name|getAttribute
argument_list|(
name|atts
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|svName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"missing mandatory sv:name attribute of element sv:node"
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// process current node first
name|ImportState
name|current
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|// need to start current node
if|if
condition|(
operator|!
name|current
operator|.
name|started
condition|)
block|{
name|processNode
argument_list|(
name|current
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|current
operator|.
name|started
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// push new ImportState instance onto the stack
name|ImportState
name|state
init|=
operator|new
name|ImportState
argument_list|()
decl_stmt|;
try|try
block|{
name|state
operator|.
name|nodeName
operator|=
operator|new
name|NameInfo
argument_list|(
name|svName
argument_list|)
operator|.
name|getRepoQualifiedName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"illegal node name: "
operator|+
name|svName
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"property"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:property element
comment|// reset temp fields
name|currentPropValues
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// property name (value of sv:name attribute)
name|String
name|svName
init|=
name|getAttribute
argument_list|(
name|atts
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|svName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"missing mandatory sv:name attribute of element sv:property"
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|currentPropName
operator|=
operator|new
name|NameInfo
argument_list|(
name|svName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"illegal property name: "
operator|+
name|svName
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
comment|// property type (sv:type attribute)
name|String
name|type
init|=
name|getAttribute
argument_list|(
name|atts
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|,
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"missing mandatory sv:type attribute of element sv:property"
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|currentPropType
operator|=
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"Unknown property type: "
operator|+
name|type
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
comment|// 'multi-value' hint (sv:multiple attribute)
name|String
name|multiple
init|=
name|getAttribute
argument_list|(
name|atts
argument_list|,
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|,
literal|"multiple"
argument_list|)
decl_stmt|;
if|if
condition|(
name|multiple
operator|!=
literal|null
condition|)
block|{
name|currentPropMultipleStatus
operator|=
name|PropInfo
operator|.
name|MultipleStatus
operator|.
name|MULTIPLE
expr_stmt|;
block|}
else|else
block|{
name|currentPropMultipleStatus
operator|=
name|PropInfo
operator|.
name|MultipleStatus
operator|.
name|UNKNOWN
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"value"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:value element
name|boolean
name|base64
init|=
name|currentPropType
operator|==
name|PropertyType
operator|.
name|BINARY
operator|||
literal|"xs:base64Binary"
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"xsi:type"
argument_list|)
argument_list|)
decl_stmt|;
name|currentPropValue
operator|=
operator|new
name|BufferedStringValue
argument_list|(
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
argument_list|,
name|currentNamePathMapper
argument_list|()
argument_list|,
name|base64
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"Unexpected element in system view xml document: {"
operator|+
name|namespaceURI
operator|+
literal|'}'
operator|+
name|localName
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|currentPropValue
operator|!=
literal|null
condition|)
block|{
comment|// property value (character data of sv:value element)
try|try
block|{
name|currentPropValue
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"error while processing property value"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|currentPropValue
operator|!=
literal|null
condition|)
block|{
comment|// property value
comment|// data reported by the ignorableWhitespace event within
comment|// sv:value tags is considered part of the value
try|try
block|{
name|currentPropValue
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"error while processing property value"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// check element name
name|ImportState
name|state
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"node"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:node element
if|if
condition|(
operator|!
name|state
operator|.
name|started
condition|)
block|{
comment|// need to start& end current node
name|processNode
argument_list|(
name|state
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|state
operator|.
name|started
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// need to end current node
name|processNode
argument_list|(
name|state
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// pop current state from stack
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"property"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:property element
comment|// check if all system properties (jcr:primaryType, jcr:uuid etc.)
comment|// have been collected and create node as necessary primaryType
if|if
condition|(
name|isSystemProperty
argument_list|(
literal|"primaryType"
argument_list|)
condition|)
block|{
name|BufferedStringValue
name|val
init|=
name|currentPropValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|val
operator|.
name|retrieve
argument_list|()
expr_stmt|;
name|state
operator|.
name|nodeTypeName
operator|=
operator|new
name|NameInfo
argument_list|(
name|s
argument_list|)
operator|.
name|getRepoQualifiedName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"illegal node type name: "
operator|+
name|s
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"illegal node type name: "
operator|+
name|s
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|isSystemProperty
argument_list|(
literal|"mixinTypes"
argument_list|)
condition|)
block|{
if|if
condition|(
name|state
operator|.
name|mixinNames
operator|==
literal|null
condition|)
block|{
name|state
operator|.
name|mixinNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|currentPropValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|BufferedStringValue
name|val
range|:
name|currentPropValues
control|)
block|{
name|String
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|val
operator|.
name|retrieve
argument_list|()
expr_stmt|;
name|state
operator|.
name|mixinNames
operator|.
name|add
argument_list|(
operator|new
name|NameInfo
argument_list|(
name|s
argument_list|)
operator|.
name|getRepoQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"error while retrieving value"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"illegal mixin type name: "
operator|+
name|s
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|isSystemProperty
argument_list|(
literal|"uuid"
argument_list|)
condition|)
block|{
name|BufferedStringValue
name|val
init|=
name|currentPropValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|state
operator|.
name|uuid
operator|=
name|val
operator|.
name|retrieve
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"error while retrieving value"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|currentPropMultipleStatus
operator|==
name|PropInfo
operator|.
name|MultipleStatus
operator|.
name|UNKNOWN
operator|&&
name|currentPropValues
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|currentPropMultipleStatus
operator|=
name|PropInfo
operator|.
name|MultipleStatus
operator|.
name|MULTIPLE
expr_stmt|;
block|}
name|PropInfo
name|prop
init|=
operator|new
name|PropInfo
argument_list|(
name|currentPropName
operator|==
literal|null
condition|?
literal|null
else|:
name|currentPropName
operator|.
name|getRepoQualifiedName
argument_list|()
argument_list|,
name|currentPropType
argument_list|,
name|currentPropValues
argument_list|,
name|currentPropMultipleStatus
argument_list|)
decl_stmt|;
name|state
operator|.
name|props
operator|.
name|add
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
comment|// reset temp fields
name|currentPropValues
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
argument_list|)
operator|&&
literal|"value"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// sv:value element
name|currentPropValues
operator|.
name|add
argument_list|(
name|currentPropValue
argument_list|)
expr_stmt|;
comment|// reset temp fields
name|currentPropValue
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|InvalidSerializedDataException
argument_list|(
literal|"invalid element in system view xml document: "
operator|+
name|localName
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|isSystemProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|localName
parameter_list|)
block|{
return|return
name|currentPropName
operator|!=
literal|null
operator|&&
name|currentPropName
operator|.
name|getNamespaceUri
argument_list|()
operator|.
name|equals
argument_list|(
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|)
operator|&&
name|currentPropName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------< inner classes>
comment|/**      * The state of parsing the XML stream.      */
specifier|static
class|class
name|ImportState
block|{
comment|/**          * name of current node          */
name|String
name|nodeName
decl_stmt|;
comment|/**          * primary type of current node          */
name|String
name|nodeTypeName
decl_stmt|;
comment|/**          * list of mixin types of current node          */
name|List
argument_list|<
name|String
argument_list|>
name|mixinNames
decl_stmt|;
comment|/**          * uuid of current node          */
name|String
name|uuid
decl_stmt|;
comment|/**          * list of PropInfo instances representing properties of current node          */
specifier|final
name|List
argument_list|<
name|PropInfo
argument_list|>
name|props
init|=
operator|new
name|ArrayList
argument_list|<
name|PropInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|/**          * flag indicating whether startNode() has been called for current node          */
name|boolean
name|started
decl_stmt|;
block|}
comment|//-------------------------------------------------------------< private>
comment|/**      * Returns the value of the named XML attribute.      *      * @param attributes set of XML attributes      * @param namespaceUri attribute namespace      * @param localName attribute local name      * @return attribute value,      *         or {@code null} if the named attribute is not found      */
specifier|private
specifier|static
name|String
name|getAttribute
parameter_list|(
name|Attributes
name|attributes
parameter_list|,
name|String
name|namespaceUri
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|getValue
argument_list|(
name|namespaceUri
argument_list|,
name|localName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

