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
name|commons
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|FileInputStream
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Iterator
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
name|Set
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
name|Strings
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
name|collect
operator|.
name|AbstractIterator
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
name|collect
operator|.
name|Iterators
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
name|collect
operator|.
name|PeekingIterator
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
name|LineIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|Sets
operator|.
name|newHashSet
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
name|io
operator|.
name|Closeables
operator|.
name|close
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
name|io
operator|.
name|FileWriteMode
operator|.
name|APPEND
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
name|io
operator|.
name|Files
operator|.
name|asByteSink
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
name|io
operator|.
name|Files
operator|.
name|move
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
name|io
operator|.
name|Files
operator|.
name|newWriter
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|io
operator|.
name|File
operator|.
name|createTempFile
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
name|forceDelete
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
name|IOUtils
operator|.
name|closeQuietly
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
name|IOUtils
operator|.
name|copyLarge
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
name|LineIterator
operator|.
name|closeQuietly
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
name|sort
operator|.
name|EscapeUtils
operator|.
name|escapeLineBreak
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
name|sort
operator|.
name|EscapeUtils
operator|.
name|unescapeLineBreaks
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
name|sort
operator|.
name|ExternalSort
operator|.
name|mergeSortedFiles
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
name|sort
operator|.
name|ExternalSort
operator|.
name|sortInBatch
import|;
end_import

begin_comment
comment|/**  * Simple File utils  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FileIOUtils
block|{
specifier|private
name|FileIOUtils
parameter_list|()
block|{     }
specifier|public
specifier|final
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|lexComparator
init|=
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
return|return
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * @deprecated use {@link java.util.function.Function#identity()} instead      */
annotation|@
name|Deprecated
specifier|public
specifier|final
specifier|static
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|passThruTransformer
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
return|return
name|input
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Sorts the given file externally using the {@link #lexComparator} and removes duplicates.      *      * @param file file whose contents needs to be sorted      */
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|sorted
init|=
name|createTempFile
argument_list|(
literal|"fleioutilssort"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|sortInBatch
argument_list|(
name|file
argument_list|,
name|lexComparator
argument_list|,
literal|true
argument_list|)
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
name|move
argument_list|(
name|sorted
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sorts the given file externally with the given comparator and removes duplicates.      *      * @param file file whose contents needs to be sorted      * @param comparator to compare      * @throws IOException      */
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|File
name|file
parameter_list|,
name|Comparator
argument_list|<
name|String
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|sorted
init|=
name|createTempFile
argument_list|(
literal|"fleioutilssort"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|merge
argument_list|(
name|sortInBatch
argument_list|(
name|file
argument_list|,
name|comparator
argument_list|,
literal|true
argument_list|)
argument_list|,
name|sorted
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|move
argument_list|(
name|sorted
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
comment|/**      * Merges a list of files after sorting with the {@link #lexComparator}.      *      * @param files files to merge      * @param output merge output file      * @throws IOException      */
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|,
name|File
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|mergeSortedFiles
argument_list|(
name|files
argument_list|,
name|output
argument_list|,
name|lexComparator
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Merges a list of files after sorting with the given comparator.      *      * @param files files to merge      * @param output merge output file      * @throws IOException      */
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|,
name|File
name|output
parameter_list|,
name|Comparator
argument_list|<
name|String
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|mergeSortedFiles
argument_list|(
name|files
argument_list|,
name|output
argument_list|,
name|comparator
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**       * Copies an input stream to a file.      *      * @param stream steam to copy      * @return      * @throws IOException      */
specifier|public
specifier|static
name|File
name|copy
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|createTempFile
argument_list|(
literal|"fleioutilscopy"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|copyInputStreamToFile
argument_list|(
name|stream
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
comment|/**      * Appends the contents of the list of files to the given file and deletes the files      * if the delete flag is enabled.      *      * If there is a scope for lines in the files containing line break characters it should be      * ensured that the files are written with {@link #writeAsLine(BufferedWriter, String, boolean)}      * with true to escape line break characters.      * @param files      * @param appendTo      * @throws IOException      */
specifier|public
specifier|static
name|void
name|append
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|,
name|File
name|appendTo
parameter_list|,
name|boolean
name|delete
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
try|try
block|{
name|appendStream
operator|=
name|asByteSink
argument_list|(
name|appendTo
argument_list|,
name|APPEND
argument_list|)
operator|.
name|openBufferedStream
argument_list|()
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|InputStream
name|iStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|copyLarge
argument_list|(
name|iStream
argument_list|,
name|appendStream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeQuietly
argument_list|(
name|iStream
argument_list|)
expr_stmt|;
block|}
block|}
name|threw
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|delete
condition|)
block|{
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|close
argument_list|(
name|appendStream
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Writes a string as a new line into the given buffered writer and optionally      * escapes the line for line breaks.      *      * @param writer to write the string      * @param str the string to write      * @param escape whether to escape string for line breaks      * @throws IOException      */
specifier|public
specifier|static
name|void
name|writeAsLine
parameter_list|(
name|BufferedWriter
name|writer
parameter_list|,
name|String
name|str
parameter_list|,
name|boolean
name|escape
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|escape
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|escapeLineBreak
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
block|}
comment|/**      * Writes string from the given iterator to the given file and optionally      * escape the written strings for line breaks.      *      * @param iterator the source of the strings      * @param f file to write to      * @param escape whether to escape for line breaks      * @return count      * @throws IOException      */
specifier|public
specifier|static
name|int
name|writeStrings
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|,
name|File
name|f
parameter_list|,
name|boolean
name|escape
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeStrings
argument_list|(
name|iterator
argument_list|,
name|f
argument_list|,
name|escape
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**      * Writes string from the given iterator to the given file and optionally      * escape the written strings for line breaks.      *      * @param iterator the source of the strings      * @param f file to write to      * @param escape escape whether to escape for line breaks      * @param logger logger to log progress      * @param message message to log      * @return      * @throws IOException      */
specifier|public
specifier|static
name|int
name|writeStrings
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|,
name|File
name|f
parameter_list|,
name|boolean
name|escape
parameter_list|,
annotation|@
name|Nullable
name|Logger
name|logger
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeStrings
argument_list|(
name|iterator
argument_list|,
name|f
argument_list|,
name|escape
argument_list|,
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|logger
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/**      * Writes string from the given iterator to the given file and optionally      * escape the written strings for line breaks.      *      * @param iterator the source of the strings      * @param f file to write to      * @param escape escape whether to escape for line breaks      * @param transformer any transformation on the input      * @param logger logger to log progress      * @param message message to log      * @return      * @throws IOException      */
specifier|public
specifier|static
name|int
name|writeStrings
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|,
name|File
name|f
parameter_list|,
name|boolean
name|escape
parameter_list|,
annotation|@
name|NotNull
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transformer
parameter_list|,
annotation|@
name|Nullable
name|Logger
name|logger
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedWriter
name|writer
init|=
name|newWriter
argument_list|(
name|f
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|writeAsLine
argument_list|(
name|writer
argument_list|,
name|transformer
operator|.
name|apply
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|escape
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|message
argument_list|)
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|threw
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|writer
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**      * @deprecated use {@link #writeStrings(Iterator, File, boolean, java.util.function.Function, Logger, String)} instead      */
annotation|@
name|Deprecated
specifier|public
specifier|static
name|int
name|writeStrings
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|,
name|File
name|f
parameter_list|,
name|boolean
name|escape
parameter_list|,
annotation|@
name|NotNull
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transformer
parameter_list|,
annotation|@
name|Nullable
name|Logger
name|logger
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8677"
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tr2
init|=
parameter_list|(
name|s
parameter_list|)
lambda|->
name|transformer
operator|.
name|apply
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|writeStrings
argument_list|(
name|iterator
argument_list|,
name|f
argument_list|,
name|escape
argument_list|,
name|tr2
argument_list|,
name|logger
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/**      * Reads strings from the given stream into a set and optionally unescaping for line breaks.      *      * @param stream the source of the strings      * @param unescape whether to unescape for line breaks      * @return set      * @throws IOException      */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|readStringsAsSet
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|boolean
name|unescape
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|boolean
name|threw
init|=
literal|true
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|unescape
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|unescapeLineBreaks
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|threw
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|reader
argument_list|,
name|threw
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
comment|/**      * Composing iterator which unescapes for line breaks and delegates to the given comparator.      * When using this it should be ensured that the data source has been correspondingly escaped.      *      * @param delegate the actual comparison iterator      * @return comparator aware of line breaks      */
specifier|public
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|lineBreakAwareComparator
parameter_list|(
name|Comparator
argument_list|<
name|String
argument_list|>
name|delegate
parameter_list|)
block|{
return|return
operator|new
name|FileIOUtils
operator|.
name|TransformingComparator
argument_list|(
name|delegate
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
return|return
name|unescapeLineBreaks
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      *      * Copy the input stream to the given file. Delete the file in case of exception.      *      * @param source the input stream source      * @param destination the file to write to      * @throws IOException      */
specifier|public
specifier|static
name|void
name|copyInputStreamToFile
parameter_list|(
specifier|final
name|InputStream
name|source
parameter_list|,
specifier|final
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|copyInputStreamToFile
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|forceDelete
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Decorates the given comparator and applies the function before delegating to the decorated      * comparator.      */
specifier|public
specifier|static
class|class
name|TransformingComparator
implements|implements
name|Comparator
argument_list|<
name|String
argument_list|>
block|{
specifier|private
name|Comparator
name|delegate
decl_stmt|;
specifier|private
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|func
decl_stmt|;
specifier|public
name|TransformingComparator
parameter_list|(
name|Comparator
name|delegate
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|func
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|func
operator|=
name|func
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|compare
argument_list|(
name|func
operator|.
name|apply
argument_list|(
name|s1
argument_list|)
argument_list|,
name|func
operator|.
name|apply
argument_list|(
name|s2
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * FileLineDifferenceIterator class which iterates over the difference of 2 files line by line.      *      * If there is a scope for lines in the files containing line break characters it should be      * ensured that both the files are written with      * {@link #writeAsLine(BufferedWriter, String, boolean)} with true to escape line break      * characters.      *       * @deprecated use {@link org.apache.jackrabbit.oak.commons.io.FileLineDifferenceIterator} instead      */
annotation|@
name|Deprecated
specifier|public
specifier|static
class|class
name|FileLineDifferenceIterator
extends|extends
name|AbstractIterator
argument_list|<
name|String
argument_list|>
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|PeekingIterator
argument_list|<
name|String
argument_list|>
name|peekMarked
decl_stmt|;
specifier|private
specifier|final
name|LineIterator
name|marked
decl_stmt|;
specifier|private
specifier|final
name|LineIterator
name|all
decl_stmt|;
specifier|private
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transformer
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|input
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|FileLineDifferenceIterator
parameter_list|(
name|LineIterator
name|marked
parameter_list|,
name|LineIterator
name|available
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|marked
argument_list|,
name|available
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileLineDifferenceIterator
parameter_list|(
name|File
name|marked
parameter_list|,
name|File
name|available
parameter_list|,
annotation|@
name|Nullable
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transformer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|marked
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|lineIterator
argument_list|(
name|available
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|transformer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileLineDifferenceIterator
parameter_list|(
name|LineIterator
name|marked
parameter_list|,
name|LineIterator
name|available
parameter_list|,
annotation|@
name|Nullable
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|transformer
parameter_list|)
throws|throws
name|IOException
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8676"
argument_list|)
expr_stmt|;
name|this
operator|.
name|marked
operator|=
name|marked
expr_stmt|;
name|this
operator|.
name|peekMarked
operator|=
name|Iterators
operator|.
name|peekingIterator
argument_list|(
name|marked
argument_list|)
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|available
expr_stmt|;
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
name|String
name|diff
init|=
name|computeNextDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|null
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
return|return
name|endOfData
argument_list|()
return|;
block|}
return|return
name|diff
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|LineIterator
operator|.
name|closeQuietly
argument_list|(
name|marked
argument_list|)
expr_stmt|;
name|LineIterator
operator|.
name|closeQuietly
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|computeNextDiff
parameter_list|()
block|{
if|if
condition|(
operator|!
name|all
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//Marked finish the rest of all are part of diff
if|if
condition|(
operator|!
name|peekMarked
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|all
operator|.
name|next
argument_list|()
return|;
block|}
name|String
name|diff
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|all
operator|.
name|hasNext
argument_list|()
operator|&&
name|diff
operator|==
literal|null
condition|)
block|{
name|diff
operator|=
name|all
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|peekMarked
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|marked
init|=
name|peekMarked
operator|.
name|peek
argument_list|()
decl_stmt|;
name|int
name|comparisonResult
init|=
name|transformer
operator|.
name|apply
argument_list|(
name|diff
argument_list|)
operator|.
name|compareTo
argument_list|(
name|transformer
operator|.
name|apply
argument_list|(
operator|(
name|marked
operator|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparisonResult
operator|>
literal|0
condition|)
block|{
comment|//Extra entries in marked. Ignore them and move on
name|peekMarked
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|comparisonResult
operator|==
literal|0
condition|)
block|{
comment|//Matching entry found in marked move past it. Not a
comment|//dif candidate
name|peekMarked
operator|.
name|next
argument_list|()
expr_stmt|;
name|diff
operator|=
literal|null
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|//This entry is not found in marked entries
comment|//hence part of diff
return|return
name|diff
return|;
block|}
block|}
block|}
return|return
name|diff
return|;
block|}
block|}
comment|/**      * Implements a {@link java.io.Closeable} wrapper over a {@link LineIterator}.      * Also has a transformer to transform the output. If the underlying file is      * provide then it deletes the file on {@link #close()}.      *      * If there is a scope for lines in the file containing line break characters it should be      * ensured that the files is written with      * {@link #writeAsLine(BufferedWriter, String, boolean)} with true to escape line break      * characters and should be properly unescaped on read.      * A custom transformer can also be provided to unescape.      *      * @param<T> the type of elements in the iterator      * @deprecated use {@link org.apache.jackrabbit.oak.commons.io.BurnOnCloseFileIterator} instead      */
annotation|@
name|Deprecated
specifier|public
specifier|static
class|class
name|BurnOnCloseFileIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractIterator
argument_list|<
name|T
argument_list|>
implements|implements
name|Closeable
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
specifier|final
name|LineIterator
name|iterator
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|transformer
decl_stmt|;
specifier|private
specifier|final
name|File
name|backingFile
decl_stmt|;
specifier|public
name|BurnOnCloseFileIterator
parameter_list|(
name|LineIterator
name|iterator
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|transformer
parameter_list|)
block|{
name|this
argument_list|(
name|iterator
argument_list|,
literal|null
argument_list|,
name|transformer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BurnOnCloseFileIterator
parameter_list|(
name|LineIterator
name|iterator
parameter_list|,
name|File
name|backingFile
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|transformer
parameter_list|)
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8666"
argument_list|)
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
name|this
operator|.
name|backingFile
operator|=
name|backingFile
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|T
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|transformer
operator|.
name|apply
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error closing iterator"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closeQuietly
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
if|if
condition|(
name|backingFile
operator|!=
literal|null
operator|&&
name|backingFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|forceDelete
argument_list|(
name|backingFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|BurnOnCloseFileIterator
argument_list|<
name|String
argument_list|>
name|wrap
parameter_list|(
name|LineIterator
name|iter
parameter_list|)
block|{
return|return
operator|new
name|BurnOnCloseFileIterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|iter
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BurnOnCloseFileIterator
argument_list|<
name|String
argument_list|>
name|wrap
parameter_list|(
name|LineIterator
name|iter
parameter_list|,
name|File
name|backingFile
parameter_list|)
block|{
return|return
operator|new
name|BurnOnCloseFileIterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|iter
argument_list|,
name|backingFile
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

