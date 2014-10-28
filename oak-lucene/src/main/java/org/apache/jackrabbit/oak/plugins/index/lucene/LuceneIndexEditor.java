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
name|lucene
package|;
end_package

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
name|getName
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newFulltextField
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPathField
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPropertyField
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|TermFactory
operator|.
name|newPathTerm
import|;
end_import

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
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
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
name|nodetype
operator|.
name|TypePredicate
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LongField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|SortedDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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

begin_comment
comment|/**  * {@link IndexEditor} implementation that is responsible for keeping the  * {@link LuceneIndex} up to date  *   * @see LuceneIndex  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexEditor
implements|implements
name|IndexEditor
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
name|LuceneIndexEditor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexEditorContext
name|context
decl_stmt|;
comment|/** Name of this node, or {@code null} for the root node. */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Parent editor or {@code null} if this is the root editor. */
specifier|private
specifier|final
name|LuceneIndexEditor
name|parent
decl_stmt|;
comment|/** Path of this editor, built lazily in {@link #getPath()}. */
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
name|boolean
name|propertiesChanged
init|=
literal|false
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|Predicate
name|typePredicate
decl_stmt|;
name|LuceneIndexEditor
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|)
throws|throws
name|CommitFailedException
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
name|context
operator|=
operator|new
name|LuceneIndexEditorContext
argument_list|(
name|definition
argument_list|,
name|analyzer
argument_list|,
name|updateCallback
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getDefinition
argument_list|()
operator|.
name|hasDeclaredNodeTypes
argument_list|()
condition|)
block|{
name|typePredicate
operator|=
operator|new
name|TypePredicate
argument_list|(
name|root
argument_list|,
name|context
operator|.
name|getDefinition
argument_list|()
operator|.
name|getDeclaringNodeTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typePredicate
operator|=
name|Predicates
operator|.
name|alwaysTrue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|LuceneIndexEditor
parameter_list|(
name|LuceneIndexEditor
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
name|context
operator|=
name|parent
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|parent
operator|.
name|root
expr_stmt|;
name|this
operator|.
name|typePredicate
operator|=
name|parent
operator|.
name|typePredicate
expr_stmt|;
block|}
specifier|public
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
throws|throws
name|CommitFailedException
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
name|String
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|addOrUpdate
argument_list|(
name|path
argument_list|,
name|after
argument_list|,
name|before
operator|.
name|exists
argument_list|()
argument_list|)
condition|)
block|{
name|long
name|indexed
init|=
name|context
operator|.
name|incIndexedNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexed
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Indexed {} nodes..."
argument_list|,
name|indexed
argument_list|)
expr_stmt|;
block|}
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
name|context
operator|.
name|closeWriter
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
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|4
argument_list|,
literal|"Failed to close the Lucene index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|context
operator|.
name|getIndexedNodes
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Indexed {} nodes, done."
argument_list|,
name|context
operator|.
name|getIndexedNodes
argument_list|()
argument_list|)
expr_stmt|;
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
name|LuceneIndexEditor
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
name|LuceneIndexEditor
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
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
name|context
operator|.
name|getWriter
argument_list|()
decl_stmt|;
comment|// Remove all index entries in the removed subtree
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|indexUpdate
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
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|5
argument_list|,
literal|"Failed to remove the index entries of"
operator|+
literal|" the removed subtree "
operator|+
name|path
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
name|boolean
name|addOrUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
throws|throws
name|CommitFailedException
block|{
try|try
block|{
name|Document
name|d
init|=
name|makeDocument
argument_list|(
name|path
argument_list|,
name|state
argument_list|,
name|isUpdate
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getWriter
argument_list|()
operator|.
name|updateDocument
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
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
literal|"Lucene"
argument_list|,
literal|3
argument_list|,
literal|"Failed to index the node "
operator|+
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|Document
name|makeDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|//TODO Possibly we can add support for compound properties like foo/bar
comment|//i.e. support for relative path restrictions
comment|// Check for declaringNodeType validity
if|if
condition|(
operator|!
name|typePredicate
operator|.
name|apply
argument_list|(
name|state
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
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
name|String
name|pname
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isVisible
argument_list|(
name|pname
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|context
operator|.
name|getDefinition
argument_list|()
operator|.
name|isOrdered
argument_list|(
name|pname
argument_list|)
condition|)
block|{
name|dirty
operator|=
name|addTypedOrderedFields
argument_list|(
name|fields
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|includeProperty
argument_list|(
name|pname
argument_list|)
condition|)
block|{
comment|//In case of fulltext we also check if given type is enabled for indexing
comment|//TODO Use context.includePropertyType however that cause issue. Need
comment|//to make filtering based on type consistent both on indexing side and
comment|//query size
if|if
condition|(
name|context
operator|.
name|isFullTextEnabled
argument_list|()
operator|&&
operator|(
name|context
operator|.
name|getPropertyTypes
argument_list|()
operator|&
operator|(
literal|1
operator|<<
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
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
name|this
operator|.
name|context
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|fields
operator|.
name|addAll
argument_list|(
name|newBinary
argument_list|(
name|property
argument_list|,
name|state
argument_list|,
name|path
operator|+
literal|"@"
operator|+
name|pname
argument_list|)
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|context
operator|.
name|isFullTextEnabled
argument_list|()
operator|&&
name|FieldFactory
operator|.
name|canCreateTypedField
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|dirty
operator|=
name|addTypedFields
argument_list|(
name|fields
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|value
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
name|this
operator|.
name|context
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|pname
argument_list|,
name|value
argument_list|,
operator|!
name|context
operator|.
name|skipTokenization
argument_list|(
name|pname
argument_list|)
argument_list|,
name|context
operator|.
name|isStored
argument_list|(
name|pname
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|isFullTextEnabled
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|isUpdate
operator|&&
operator|!
name|dirty
condition|)
block|{
comment|// updated the state but had no relevant changes
return|return
literal|null
return|;
block|}
comment|//For property index no use making an empty document if
comment|//none of the properties are indexed
if|if
condition|(
operator|!
name|context
operator|.
name|isFullTextEnabled
argument_list|()
operator|&&
operator|!
name|dirty
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|//TODO Possibly index nodeName without tokenization for node name based queries
if|if
condition|(
name|context
operator|.
name|isFullTextEnabled
argument_list|()
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Field
name|f
range|:
name|fields
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
specifier|private
name|boolean
name|addTypedFields
parameter_list|(
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|,
name|PropertyState
name|property
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|int
name|tag
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|fieldAdded
init|=
literal|false
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
name|property
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|f
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|LONG
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|LongField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DATE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|String
name|date
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|LongField
argument_list|(
name|name
argument_list|,
name|FieldFactory
operator|.
name|dateToLong
argument_list|(
name|date
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DOUBLE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|DoubleField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|BOOLEAN
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|StringField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|context
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|fieldAdded
return|;
block|}
specifier|private
name|boolean
name|addTypedOrderedFields
parameter_list|(
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|,
name|PropertyState
name|property
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|int
name|tag
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|FieldNames
operator|.
name|createDocValFieldName
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|fieldAdded
init|=
literal|false
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
name|property
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|f
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|LONG
operator|.
name|tag
argument_list|()
condition|)
block|{
comment|//TODO Distinguish fields which need to be used for search and for sort
comment|//If a field is only used for Sort then it can be stored with less precision
name|f
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DATE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|String
name|date
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
name|name
argument_list|,
name|FieldFactory
operator|.
name|dateToLong
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DOUBLE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|BOOLEAN
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|SortedDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|SortedDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|context
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|fieldAdded
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVisible
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|':'
return|;
block|}
specifier|private
name|List
argument_list|<
name|Field
argument_list|>
name|newBinary
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
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
name|fields
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|parseStringValue
argument_list|(
name|v
argument_list|,
name|metadata
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
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
parameter_list|,
name|String
name|path
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
name|context
operator|.
name|getParser
argument_list|()
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
name|path
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

