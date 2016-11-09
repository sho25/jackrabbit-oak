begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
operator|.
name|writer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|FieldNames
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
name|lucene
operator|.
name|IndexDefinition
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
name|lucene
operator|.
name|LuceneIndexConstants
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
name|lucene
operator|.
name|OakDirectory
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
name|lucene
operator|.
name|util
operator|.
name|SuggestHelper
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|PerFieldAnalyzerWrapper
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
name|shingle
operator|.
name|ShingleAnalyzerWrapper
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
name|IndexWriterConfig
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
name|SerialMergeScheduler
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_FILE
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
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_NAME
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
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_PATH
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
name|LuceneIndexConstants
operator|.
name|VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NoLockFactory
operator|.
name|getNoLockFactory
import|;
end_import

begin_class
specifier|public
class|class
name|IndexWriterUtils
block|{
specifier|public
specifier|static
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|boolean
name|remoteDir
parameter_list|)
block|{
comment|// FIXME: Hack needed to make Lucene work in an OSGi environment
name|Thread
name|thread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|thread
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Analyzer
name|definitionAnalyzer
init|=
name|definition
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|()
decl_stmt|;
name|analyzers
operator|.
name|put
argument_list|(
name|FieldNames
operator|.
name|SPELLCHECK
argument_list|,
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANALYZER
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|definition
operator|.
name|isSuggestAnalyzed
argument_list|()
condition|)
block|{
name|analyzers
operator|.
name|put
argument_list|(
name|FieldNames
operator|.
name|SUGGEST
argument_list|,
name|SuggestHelper
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|definitionAnalyzer
argument_list|,
name|analyzers
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteDir
condition|)
block|{
name|config
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|definition
operator|.
name|getCodec
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setCodec
argument_list|(
name|definition
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
finally|finally
block|{
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Directory
name|newIndexDirectory
parameter_list|(
name|IndexDefinition
name|indexDefinition
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|dirName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_FILE
operator|.
name|equalsIgnoreCase
argument_list|(
name|definition
operator|.
name|getString
argument_list|(
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|path
operator|=
name|definition
operator|.
name|getString
argument_list|(
name|PERSISTENCE_PATH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|OakDirectory
argument_list|(
name|definition
argument_list|,
name|dirName
argument_list|,
name|indexDefinition
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
comment|// try {
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// TODO: close() is never called
comment|// TODO: no locking used
comment|// --> using the FS backend for the index is in any case
comment|// troublesome in clustering scenarios and for backup
comment|// etc. so instead of fixing these issues we'd better
comment|// work on making the in-content index work without
comment|// problems (or look at the Solr indexer as alternative)
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|,
name|getNoLockFactory
argument_list|()
argument_list|)
return|;
comment|// } catch (IOException e) {
comment|// throw new CommitFailedException("Lucene", 1,
comment|// "Failed to open the index in " + path, e);
comment|// }
block|}
block|}
block|}
end_class

end_unit

