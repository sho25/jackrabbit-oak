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
name|segment
operator|.
name|tool
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
name|Preconditions
operator|.
name|checkArgument
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
name|Preconditions
operator|.
name|checkNotNull
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
name|util
operator|.
name|Iterator
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
name|segment
operator|.
name|SegmentNodeStorePersistence
operator|.
name|JournalFile
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|LocalJournalFile
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
name|segment
operator|.
name|file
operator|.
name|tooling
operator|.
name|RevisionHistory
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
name|segment
operator|.
name|file
operator|.
name|tooling
operator|.
name|RevisionHistory
operator|.
name|HistoryElement
import|;
end_import

begin_comment
comment|/**  * Prints the revision history of an existing segment store. Optionally, it can  * narrow to the output to the history of a single node.  */
end_comment

begin_class
specifier|public
class|class
name|History
block|{
comment|/**      * Create a builder for the {@link History} command.      *      * @return an instance of {@link Builder}.      */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Collect options for the {@link History} command.      */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|path
decl_stmt|;
specifier|private
name|File
name|journal
decl_stmt|;
specifier|private
name|String
name|node
decl_stmt|;
specifier|private
name|int
name|depth
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The path to an existing segment store. This parameter is required.          *          * @param path the path to an existing segment store.          * @return this builder.          */
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * The path to the journal. This parameter is required.          *          * @param journal the path to the journal.          * @return this builder.          */
specifier|public
name|Builder
name|withJournal
parameter_list|(
name|File
name|journal
parameter_list|)
block|{
name|this
operator|.
name|journal
operator|=
name|checkNotNull
argument_list|(
name|journal
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * A path to a node. If specified, the history will be restricted to the          * subtree pointed to by this node. This parameter is not mandatory and          * defaults to the entire tree.          *          * @param node a path to a node.          * @return this builder.          */
specifier|public
name|Builder
name|withNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Maximum depth of the history. If specified, this command will print          * information about the history of every node at or below the provided          * depth. This parameter is not mandatory and defaults to zero.          *          * @param depth the depth of the subtree.          * @return this builder.          */
specifier|public
name|Builder
name|withDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|depth
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link History} command.          *          * @return an instance of {@link History}.          */
specifier|public
name|History
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|journal
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
operator|new
name|History
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
specifier|final
name|JournalFile
name|journal
decl_stmt|;
specifier|private
specifier|final
name|String
name|node
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
specifier|private
name|History
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|journal
operator|=
operator|new
name|LocalJournalFile
argument_list|(
name|builder
operator|.
name|journal
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|builder
operator|.
name|node
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|builder
operator|.
name|depth
expr_stmt|;
block|}
specifier|public
name|int
name|run
parameter_list|()
block|{
try|try
block|{
name|run
argument_list|(
operator|new
name|RevisionHistory
argument_list|(
name|path
argument_list|)
operator|.
name|getHistory
argument_list|(
name|journal
argument_list|,
name|node
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
specifier|private
name|void
name|run
parameter_list|(
name|Iterator
argument_list|<
name|HistoryElement
argument_list|>
name|history
parameter_list|)
block|{
while|while
condition|(
name|history
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|history
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|(
name|depth
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

