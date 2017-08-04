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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Charsets
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
name|Stopwatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|WriterOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|inventory
operator|.
name|Format
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|inventory
operator|.
name|InventoryPrinter
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
name|directory
operator|.
name|IndexConsistencyChecker
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
name|directory
operator|.
name|IndexConsistencyChecker
operator|.
name|Level
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
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
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
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|TYPE_LUCENE
import|;
end_import

begin_class
class|class
name|IndexConsistencyCheckPrinter
implements|implements
name|InventoryPrinter
block|{
specifier|private
specifier|final
name|IndexHelper
name|indexHelper
decl_stmt|;
specifier|private
specifier|final
name|Level
name|level
decl_stmt|;
specifier|public
name|IndexConsistencyCheckPrinter
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|indexHelper
operator|=
name|indexHelper
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
operator|==
literal|1
condition|?
name|Level
operator|.
name|BLOBS_ONLY
else|:
name|Level
operator|.
name|FULL
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|print
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|Format
name|format
parameter_list|,
name|boolean
name|isZip
parameter_list|)
block|{
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|indexHelper
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|validIndexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidIndexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ignoredIndexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexHelper
operator|.
name|getIndexPathService
argument_list|()
operator|.
name|getIndexPaths
argument_list|()
control|)
block|{
name|NodeState
name|indexState
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|indexState
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|ignoredIndexes
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|,
name|indexHelper
operator|.
name|getWorkDir
argument_list|()
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setPrintStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
operator|new
name|WriterOutputStream
argument_list|(
name|pw
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|IndexConsistencyChecker
operator|.
name|Result
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|result
operator|.
name|dump
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|clean
condition|)
block|{
name|validIndexes
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|invalidIndexes
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s => %s%n"
argument_list|,
name|indexPath
argument_list|,
name|result
operator|.
name|clean
condition|?
literal|"valid"
else|:
literal|"invalid<=="
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"Error occurred while performing consistency check for index [%s]%n"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|print
argument_list|(
name|validIndexes
argument_list|,
literal|"Valid indexes :"
argument_list|,
name|pw
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|invalidIndexes
argument_list|,
literal|"Invalid indexes :"
argument_list|,
name|pw
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|ignoredIndexes
argument_list|,
literal|"Ignored indexes as these are not of type lucene:"
argument_list|,
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|printf
argument_list|(
literal|"Time taken %s%n"
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
parameter_list|,
name|String
name|message
parameter_list|,
name|PrintWriter
name|pw
parameter_list|)
block|{
if|if
condition|(
operator|!
name|indexPaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|indexPaths
operator|.
name|forEach
argument_list|(
parameter_list|(
name|path
parameter_list|)
lambda|->
name|pw
operator|.
name|printf
argument_list|(
literal|"    - %s%n"
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

