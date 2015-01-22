begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|solr
operator|.
name|index
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
name|LinkedList
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
name|Blob
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
name|Type
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
name|plugins
operator|.
name|index
operator|.
name|IndexEditor
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
name|index
operator|.
name|IndexUpdateCallback
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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|CommitPolicy
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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|OakSolrConfiguration
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
name|commit
operator|.
name|Editor
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
name|NodeBuilder
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|metadata
operator|.
name|Metadata
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
name|parser
operator|.
name|AutoDetectParser
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|Parser
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
name|sax
operator|.
name|WriteOutContentHandler
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_DATA
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
import|;
end_import

begin_comment
comment|/**  * Index editor for keeping a Solr index up to date.  */
end_comment

begin_class
class|class
name|SolrIndexEditor
implements|implements
name|IndexEditor
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Parent editor, or {@code null} if this is the root editor.      */
specifier|private
specifier|final
name|SolrIndexEditor
name|parent
decl_stmt|;
comment|/**      * Name of this node, or {@code null} for the root node.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Path of this editor, built lazily in {@link #getPath()}.      */
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Index definition node builder      */
specifier|private
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
specifier|private
specifier|final
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
specifier|final
name|OakSolrConfiguration
name|configuration
decl_stmt|;
specifier|private
name|boolean
name|propertiesChanged
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|SolrIndexEditor
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|,
name|IndexUpdateCallback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|solrServer
operator|=
name|solrServer
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|callback
expr_stmt|;
block|}
specifier|private
name|SolrIndexEditor
parameter_list|(
name|SolrIndexEditor
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|parent
operator|.
name|definition
expr_stmt|;
name|this
operator|.
name|solrServer
operator|=
name|parent
operator|.
name|solrServer
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|parent
operator|.
name|configuration
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|parent
operator|.
name|updateCallback
expr_stmt|;
block|}
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
comment|// => parent != null
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|propertiesChanged
operator|||
operator|!
name|before
operator|.
name|exists
argument_list|()
condition|)
block|{
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
try|try
block|{
name|solrServer
operator|.
name|add
argument_list|(
name|docFromState
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|2
argument_list|,
literal|"Failed to add a document to Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|6
argument_list|,
literal|"Failed to send data to Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|commitByPolicy
argument_list|(
name|solrServer
argument_list|,
name|configuration
operator|.
name|getCommitPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|3
argument_list|,
literal|"Failed to commit changes to Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|6
argument_list|,
literal|"Failed to send data to Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|commitByPolicy
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|,
name|CommitPolicy
name|commitPolicy
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
switch|switch
condition|(
name|commitPolicy
condition|)
block|{
case|case
name|HARD
case|:
block|{
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|SOFT
case|:
block|{
name|solrServer
operator|.
name|commit
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|AUTO
case|:
block|{
break|break;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|propertiesChanged
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|propertiesChanged
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|propertiesChanged
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|new
name|SolrIndexEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
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
operator|new
name|SolrIndexEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|path
init|=
name|partialEscape
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|formattedQuery
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s:%s*"
argument_list|,
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"deleting by query {}"
argument_list|,
name|formattedQuery
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|deleteByQuery
argument_list|(
name|formattedQuery
argument_list|)
expr_stmt|;
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|5
argument_list|,
literal|"Failed to remove documents from Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Solr"
argument_list|,
literal|6
argument_list|,
literal|"Failed to send data to Solr"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
comment|// no need to recurse down the removed subtree
block|}
specifier|private
specifier|static
name|CharSequence
name|partialEscape
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|||
name|c
operator|==
literal|'!'
operator|||
name|c
operator|==
literal|'('
operator|||
name|c
operator|==
literal|')'
operator|||
name|c
operator|==
literal|':'
operator|||
name|c
operator|==
literal|'^'
operator|||
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|']'
operator|||
name|c
operator|==
literal|'/'
operator|||
name|c
operator|==
literal|'{'
operator|||
name|c
operator|==
literal|'}'
operator|||
name|c
operator|==
literal|'~'
operator|||
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'?'
operator|||
name|c
operator|==
literal|'-'
operator|||
name|c
operator|==
literal|' '
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
specifier|private
name|SolrInputDocument
name|docFromState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|SolrInputDocument
name|inputDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|configuration
operator|.
name|getUsedProperties
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|configuration
operator|.
name|getUsedProperties
argument_list|()
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
operator|||
operator|!
name|configuration
operator|.
name|getIgnoredProperties
argument_list|()
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// try to get the field to use for this property from configuration
name|String
name|fieldName
init|=
name|configuration
operator|.
name|getFieldNameFor
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Type
operator|.
name|BINARY
operator|.
name|tag
argument_list|()
operator|==
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|extractTextValues
argument_list|(
name|property
argument_list|,
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// or fallback to adding propertyName:stringValue(s)
for|for
control|(
name|String
name|s
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|inputDocument
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|extractTextValues
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|JCR_DATA
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|state
operator|.
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
comment|// not mandatory
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|String
name|encoding
init|=
name|state
operator|.
name|getString
argument_list|(
name|JcrConstants
operator|.
name|JCR_ENCODING
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
comment|// not mandatory
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Blob
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|parseStringValue
argument_list|(
name|v
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
specifier|private
name|String
name|parseStringValue
parameter_list|(
name|Blob
name|v
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|WriteOutContentHandler
name|handler
init|=
operator|new
name|WriteOutContentHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|v
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LinkageError
name|e
parameter_list|)
block|{
comment|// Capture and ignore errors caused by extraction libraries
comment|// not being present. This is equivalent to disabling
comment|// selected media types in configuration, so we can simply
comment|// ignore these errors.
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Capture and report any other full text extraction problems.
comment|// The special STOP exception is used for normal termination.
if|if
condition|(
operator|!
name|handler
operator|.
name|isWriteLimitReached
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Failed to extract text from a binary property: "
operator|+
literal|" This is a fairly common case, and nothing to"
operator|+
literal|" worry about. The stack trace is included to"
operator|+
literal|" help improve the text extraction feature."
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return
literal|"TextExtractionError"
return|;
block|}
block|}
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

