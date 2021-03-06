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
operator|.
name|io
package|;
end_package

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
name|closeQuietly
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
name|IOException
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
name|function
operator|.
name|Function
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|FileIOUtils
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

begin_comment
comment|/**  * FileLineDifferenceIterator class which iterates over the difference of 2 files line by line.  *  * If there is a scope for lines in the files containing line break characters it should be  * ensured that both the files are written with  * {@link FileIOUtils#writeAsLine(BufferedWriter, String, boolean)} with true to escape line break  * characters.  */
end_comment

begin_class
specifier|public
class|class
name|FileLineDifferenceIterator
implements|implements
name|Closeable
implements|,
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|Impl
name|delegate
decl_stmt|;
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
name|this
operator|.
name|delegate
operator|=
operator|new
name|Impl
argument_list|(
name|marked
argument_list|,
name|available
argument_list|,
name|transformer
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
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|this
operator|.
name|delegate
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
name|this
operator|.
name|delegate
operator|.
name|next
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
name|this
operator|.
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Impl
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
name|Impl
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
if|if
condition|(
name|marked
operator|instanceof
name|Closeable
condition|)
block|{
name|closeQuietly
argument_list|(
name|marked
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|instanceof
name|Closeable
condition|)
block|{
name|closeQuietly
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
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
comment|// Marked finish the rest of all are part of diff
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
comment|// Extra entries in marked. Ignore them and move on
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
comment|// Matching entry found in marked move past it. Not a
comment|// dif candidate
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
comment|// This entry is not found in marked entries
comment|// hence part of diff
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
block|}
end_class

end_unit

