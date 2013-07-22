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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|commons
operator|.
name|NamespaceHelper
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
name|jcr
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
name|plugins
operator|.
name|name
operator|.
name|NamespaceConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Locator
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
name|SAXParseException
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

begin_comment
comment|/**  * An {@code ImportHandler} instance can be used to import serialized  * data in System View XML or Document View XML. Processing of the XML is  * handled by specialized {@code ContentHandler}s  * (i.e. {@code SysViewImportHandler} and {@code DocViewImportHandler}).  *<p/>  * The actual task of importing though is delegated to the implementation of  * the {@code {@link Importer}} interface.  *<p/>  *<b>Important Note:</b>  *<p/>  * These SAX Event Handlers expect that Namespace URI's and local names are  * reported in the {@code start/endElement} events and that  * {@code start/endPrefixMapping} events are reported  * (i.e. default SAX2 Namespace processing).  */
end_comment

begin_class
specifier|public
class|class
name|ImportHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ImportHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
specifier|final
name|Importer
name|importer
decl_stmt|;
specifier|private
specifier|final
name|NamespaceHelper
name|helper
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isWorkspaceImport
decl_stmt|;
specifier|protected
name|Locator
name|locator
decl_stmt|;
specifier|private
name|TargetImportHandler
name|targetHandler
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tempPrefixMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ImportHandler
parameter_list|(
name|String
name|absPath
parameter_list|,
name|SessionContext
name|sessionContext
parameter_list|,
name|Root
name|root
parameter_list|,
name|int
name|uuidBehavior
parameter_list|,
name|boolean
name|isWorkspaceImport
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|helper
operator|=
operator|new
name|NamespaceHelper
argument_list|(
name|sessionContext
operator|.
name|getSession
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|importer
operator|=
operator|new
name|ImporterImpl
argument_list|(
name|absPath
argument_list|,
name|sessionContext
argument_list|,
name|root
argument_list|,
name|uuidBehavior
argument_list|,
name|isWorkspaceImport
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|isWorkspaceImport
operator|=
name|isWorkspaceImport
expr_stmt|;
block|}
comment|//---------------------------------------------------------< ErrorHandler>
annotation|@
name|Override
specifier|public
name|void
name|warning
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// log exception and carry on...
name|log
operator|.
name|warn
argument_list|(
literal|"warning encountered at line: "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|", column: "
operator|+
name|e
operator|.
name|getColumnNumber
argument_list|()
operator|+
literal|" while parsing XML stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// log exception and carry on...
name|log
operator|.
name|error
argument_list|(
literal|"error encountered at line: "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|", column: "
operator|+
name|e
operator|.
name|getColumnNumber
argument_list|()
operator|+
literal|" while parsing XML stream: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fatalError
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// log and re-throw exception
name|log
operator|.
name|error
argument_list|(
literal|"fatal error encountered at line: "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|", column: "
operator|+
name|e
operator|.
name|getColumnNumber
argument_list|()
operator|+
literal|" while parsing XML stream: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//-------------------------------------------------------< ContentHandler>
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|// delegate to target handler
if|if
condition|(
name|targetHandler
operator|!=
literal|null
condition|)
block|{
name|targetHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isWorkspaceImport
condition|)
block|{
try|try
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|sessionContext
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Records the given namespace mapping to be included in the local      * namespace context. The local namespace context is instantiated      * in {@link #startElement(String, String, String, Attributes)} using      * all the the namespace mappings recorded for the current XML element.      *<p/>      * The namespace is also recorded in the persistent namespace registry      * unless it is already known.      *      * @param prefix namespace prefix      * @param uri    namespace URI      */
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|helper
operator|.
name|registerNamespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetHandler
operator|!=
literal|null
condition|)
block|{
name|targetHandler
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tempPrefixMap
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
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
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|targetHandler
operator|!=
literal|null
condition|)
block|{
name|targetHandler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tempPrefixMap
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|targetHandler
operator|==
literal|null
condition|)
block|{
comment|// the namespace of the first element determines the type of XML
comment|// (system view/document view)
if|if
condition|(
name|NamespaceConstants
operator|.
name|NAMESPACE_SV
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
name|targetHandler
operator|=
operator|new
name|SysViewImportHandler
argument_list|(
name|importer
argument_list|,
name|valueFactory
argument_list|,
name|helper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|targetHandler
operator|=
operator|new
name|DocViewImportHandler
argument_list|(
name|importer
argument_list|,
name|valueFactory
argument_list|,
name|helper
argument_list|)
expr_stmt|;
block|}
name|targetHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
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
name|prefixMapping
range|:
name|tempPrefixMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|targetHandler
operator|.
name|startPrefixMapping
argument_list|(
name|prefixMapping
operator|.
name|getKey
argument_list|()
argument_list|,
name|prefixMapping
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// delegate to target handler
name|targetHandler
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
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
comment|// delegate to target handler
name|targetHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegates the call to the underlying target handler and asks the      * handler to end the current namespace context.      * {@inheritDoc}      */
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
name|targetHandler
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|this
operator|.
name|locator
operator|=
name|locator
expr_stmt|;
block|}
block|}
end_class

end_unit

