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
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|function
operator|.
name|Function
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
name|FileUtils
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
name|FilenameUtils
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
name|sort
operator|.
name|ExternalSort
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|ONE_GB
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
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|elements
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|NodeStateEntryWriter
operator|.
name|getPath
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateEntrySorter
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
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULTMAXTEMPFILES
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|File
name|nodeStateFile
decl_stmt|;
specifier|private
specifier|final
name|File
name|workDir
decl_stmt|;
specifier|private
specifier|final
name|Charset
name|charset
init|=
name|UTF_8
decl_stmt|;
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|pathComparator
decl_stmt|;
specifier|private
name|File
name|sortedFile
decl_stmt|;
specifier|private
name|boolean
name|useZip
decl_stmt|;
specifier|private
name|boolean
name|deleteOriginal
decl_stmt|;
specifier|private
name|long
name|maxMemory
init|=
name|ONE_GB
operator|*
literal|5
decl_stmt|;
specifier|public
name|NodeStateEntrySorter
parameter_list|(
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|pathComparator
parameter_list|,
name|File
name|nodeStateFile
parameter_list|,
name|File
name|workDir
parameter_list|)
block|{
name|this
argument_list|(
name|pathComparator
argument_list|,
name|nodeStateFile
argument_list|,
name|workDir
argument_list|,
name|getSortedFileName
argument_list|(
name|nodeStateFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeStateEntrySorter
parameter_list|(
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|pathComparator
parameter_list|,
name|File
name|nodeStateFile
parameter_list|,
name|File
name|workDir
parameter_list|,
name|File
name|sortedFile
parameter_list|)
block|{
name|this
operator|.
name|nodeStateFile
operator|=
name|nodeStateFile
expr_stmt|;
name|this
operator|.
name|workDir
operator|=
name|workDir
expr_stmt|;
name|this
operator|.
name|sortedFile
operator|=
name|sortedFile
expr_stmt|;
name|this
operator|.
name|pathComparator
operator|=
name|pathComparator
expr_stmt|;
block|}
specifier|public
name|void
name|setUseZip
parameter_list|(
name|boolean
name|useZip
parameter_list|)
block|{
name|this
operator|.
name|useZip
operator|=
name|useZip
expr_stmt|;
block|}
specifier|public
name|void
name|setDeleteOriginal
parameter_list|(
name|boolean
name|deleteOriginal
parameter_list|)
block|{
name|this
operator|.
name|deleteOriginal
operator|=
name|deleteOriginal
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxMemoryInGB
parameter_list|(
name|long
name|maxMemoryInGb
parameter_list|)
block|{
name|this
operator|.
name|maxMemory
operator|=
name|maxMemory
operator|*
name|ONE_GB
expr_stmt|;
block|}
specifier|public
name|void
name|sort
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|estimatedMemory
init|=
name|estimateAvailableMemory
argument_list|()
decl_stmt|;
name|long
name|memory
init|=
name|Math
operator|.
name|max
argument_list|(
name|estimatedMemory
argument_list|,
name|maxMemory
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sorting with memory {} (estimated {})"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|memory
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|estimatedMemory
argument_list|)
argument_list|)
expr_stmt|;
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Comparator
argument_list|<
name|NodeStateEntryHolder
argument_list|>
name|comparator
init|=
name|Comparator
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
name|Function
argument_list|<
name|String
argument_list|,
name|NodeStateEntryHolder
argument_list|>
name|func1
init|=
operator|(
name|line
operator|)
operator|->
name|line
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeStateEntryHolder
argument_list|(
name|line
argument_list|,
name|pathComparator
argument_list|)
decl_stmt|;
name|Function
argument_list|<
name|NodeStateEntryHolder
argument_list|,
name|String
argument_list|>
name|func2
init|=
name|holder
lambda|->
name|holder
operator|==
literal|null
condition|?
literal|null
else|:
name|holder
operator|.
name|getLine
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|sortedFiles
init|=
name|ExternalSort
operator|.
name|sortInBatch
argument_list|(
name|nodeStateFile
argument_list|,
name|comparator
argument_list|,
comment|//Comparator to use
name|DEFAULTMAXTEMPFILES
argument_list|,
name|memory
argument_list|,
name|charset
argument_list|,
comment|//charset
name|workDir
argument_list|,
comment|//temp directory where intermediate files are created
literal|false
argument_list|,
literal|0
argument_list|,
name|useZip
argument_list|,
name|func2
argument_list|,
name|func1
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Batch sorting done in {} with {} files of size {} to merge"
argument_list|,
name|w
argument_list|,
name|sortedFiles
operator|.
name|size
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
name|sizeOf
argument_list|(
name|sortedFiles
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteOriginal
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Removing the original file {}"
argument_list|,
name|nodeStateFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|nodeStateFile
argument_list|)
expr_stmt|;
block|}
name|Stopwatch
name|w2
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|ExternalSort
operator|.
name|mergeSortedFiles
argument_list|(
name|sortedFiles
argument_list|,
name|sortedFile
argument_list|,
name|comparator
argument_list|,
name|charset
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|useZip
argument_list|,
name|func2
argument_list|,
name|func1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Merging of sorted files completed in {}"
argument_list|,
name|w2
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sorting completed in {}"
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
specifier|public
name|File
name|getSortedFile
parameter_list|()
block|{
return|return
name|sortedFile
return|;
block|}
specifier|private
specifier|static
name|File
name|getSortedFileName
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|String
name|extension
init|=
name|FilenameUtils
operator|.
name|getExtension
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|baseName
init|=
name|FilenameUtils
operator|.
name|getBaseName
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|baseName
operator|+
literal|"-sorted."
operator|+
name|extension
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|sizeOf
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|sortedFiles
parameter_list|)
block|{
return|return
name|sortedFiles
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|File
operator|::
name|length
argument_list|)
operator|.
name|sum
argument_list|()
return|;
block|}
comment|/**      * This method calls the garbage collector and then returns the free      * memory. This avoids problems with applications where the GC hasn't      * reclaimed memory and reports no available memory.      *      * @return available memory      */
specifier|private
specifier|static
name|long
name|estimateAvailableMemory
parameter_list|()
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// http://stackoverflow.com/questions/12807797/java-get-available-memory
name|Runtime
name|r
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|long
name|allocatedMemory
init|=
name|r
operator|.
name|totalMemory
argument_list|()
operator|-
name|r
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|long
name|presFreeMemory
init|=
name|r
operator|.
name|maxMemory
argument_list|()
operator|-
name|allocatedMemory
decl_stmt|;
return|return
name|presFreeMemory
return|;
block|}
specifier|static
class|class
name|NodeStateEntryHolder
implements|implements
name|Comparable
argument_list|<
name|NodeStateEntryHolder
argument_list|>
block|{
specifier|final
name|String
name|line
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|pathElements
decl_stmt|;
specifier|final
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|comparator
decl_stmt|;
specifier|public
name|NodeStateEntryHolder
parameter_list|(
name|String
name|line
parameter_list|,
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|this
operator|.
name|pathElements
operator|=
name|copyOf
argument_list|(
name|elements
argument_list|(
name|getPath
argument_list|(
name|line
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|NodeStateEntryHolder
name|o
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|this
operator|.
name|pathElements
argument_list|,
name|o
operator|.
name|pathElements
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

