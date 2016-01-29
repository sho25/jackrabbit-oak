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
name|segment
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
name|collect
operator|.
name|Iterables
operator|.
name|transform
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
name|Lists
operator|.
name|reverse
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|byteCountToDisplaySize
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|BINARY
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
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
name|segment
operator|.
name|FileStoreHelper
operator|.
name|newBasicReadOnlyBlobStore
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
name|segment
operator|.
name|FileStoreHelper
operator|.
name|readRevisions
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
name|segment
operator|.
name|RecordId
operator|.
name|fromString
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
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|NodeStateDiff
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

begin_class
specifier|public
class|class
name|FileStoreDiff
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"java -jar oak-run-*.jar tarmkdiff<path/to/repository> [--list] [--diff=R0..R1] [--incremental] [--ignore-snfes] [--output=/path/to/output/file]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeO
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"Path to segment store (required)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|outO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"output"
argument_list|,
literal|"Output file"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
operator|new
name|File
argument_list|(
literal|"diff_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|".log"
argument_list|)
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|listOnlyO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"list"
argument_list|,
literal|"Lists available revisions"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|intervalO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"diff"
argument_list|,
literal|"Revision diff interval. Ex '--diff=R0..R1'. 'HEAD' can be used to reference the latest head revision, ie. '--diff=R0..HEAD'"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|incrementalO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"incremental"
argument_list|,
literal|"Runs diffs between each subsequent revisions in the provided interval"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|pathO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"path"
argument_list|,
literal|"Filter diff by given path"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|isgnoreSNFEsO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"ignore-snfes"
argument_list|,
literal|"Ignores SegmentNotFoundExceptions and continues running the diff (experimental)"
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|File
name|store
init|=
name|storeO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|File
name|out
init|=
name|outO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|listOnlyO
argument_list|)
condition|)
block|{
name|listRevs
argument_list|(
name|store
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diff
argument_list|(
name|store
argument_list|,
name|intervalO
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|options
operator|.
name|has
argument_list|(
name|incrementalO
argument_list|)
argument_list|,
name|out
argument_list|,
name|pathO
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|options
operator|.
name|has
argument_list|(
name|isgnoreSNFEsO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|listRevs
parameter_list|(
name|File
name|store
parameter_list|,
name|File
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Store "
operator|+
name|store
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing revisions to "
operator|+
name|out
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|revs
init|=
name|readRevisions
argument_list|(
name|store
argument_list|)
decl_stmt|;
if|if
condition|(
name|revs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No revisions found."
argument_list|)
expr_stmt|;
return|return;
block|}
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|r
range|:
name|revs
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|diff
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|interval
parameter_list|,
name|boolean
name|incremental
parameter_list|,
name|File
name|out
parameter_list|,
name|String
name|filter
parameter_list|,
name|boolean
name|ignoreSNFEs
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Store "
operator|+
name|dir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Writing diff to "
operator|+
name|out
argument_list|)
expr_stmt|;
name|String
index|[]
name|tokens
init|=
name|interval
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\.\\."
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error parsing revision interval '"
operator|+
name|interval
operator|+
literal|"'."
argument_list|)
expr_stmt|;
return|return;
block|}
name|ReadOnlyStore
name|store
init|=
operator|new
name|ReadOnlyStore
argument_list|(
name|dir
argument_list|,
name|newBasicReadOnlyBlobStore
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|idL
init|=
literal|null
decl_stmt|;
name|RecordId
name|idR
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|tokens
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"head"
argument_list|)
condition|)
block|{
name|idL
operator|=
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|idL
operator|=
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tokens
index|[
literal|1
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"head"
argument_list|)
condition|)
block|{
name|idR
operator|=
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getRecordId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|idR
operator|=
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|tokens
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error parsing revision interval '"
operator|+
name|interval
operator|+
literal|"': "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|incremental
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|revs
init|=
name|readRevisions
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating diff between "
operator|+
name|idL
operator|+
literal|" and "
operator|+
name|idR
operator|+
literal|" incrementally. Found "
operator|+
name|revs
operator|.
name|size
argument_list|()
operator|+
literal|" revisions."
argument_list|)
expr_stmt|;
name|int
name|s
init|=
name|revs
operator|.
name|indexOf
argument_list|(
name|idL
operator|.
name|toString10
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|e
init|=
name|revs
operator|.
name|indexOf
argument_list|(
name|idR
operator|.
name|toString10
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
operator|-
literal|1
operator|||
name|e
operator|==
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unable to match input revisions with FileStore."
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|revDiffs
init|=
name|revs
operator|.
name|subList
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|>
name|e
condition|)
block|{
comment|// reverse list
name|revDiffs
operator|=
name|reverse
argument_list|(
name|revDiffs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|revDiffs
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Nothing to diff: "
operator|+
name|revDiffs
argument_list|)
expr_stmt|;
return|return;
block|}
name|Iterator
argument_list|<
name|String
argument_list|>
name|revDiffsIt
init|=
name|revDiffs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|RecordId
name|idLt
init|=
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|revDiffsIt
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|revDiffsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RecordId
name|idRt
init|=
name|fromString
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|,
name|revDiffsIt
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|good
init|=
name|diff
argument_list|(
name|store
argument_list|,
name|idLt
argument_list|,
name|idRt
argument_list|,
name|filter
argument_list|,
name|pw
argument_list|)
decl_stmt|;
name|idLt
operator|=
name|idRt
expr_stmt|;
if|if
condition|(
operator|!
name|good
operator|&&
operator|!
name|ignoreSNFEs
condition|)
block|{
break|break;
block|}
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating diff between "
operator|+
name|idL
operator|+
literal|" and "
operator|+
name|idR
argument_list|)
expr_stmt|;
name|diff
argument_list|(
name|store
argument_list|,
name|idL
argument_list|,
name|idR
argument_list|,
name|filter
argument_list|,
name|pw
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|long
name|dur
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Finished in "
operator|+
name|dur
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|diff
parameter_list|(
name|ReadOnlyStore
name|store
parameter_list|,
name|RecordId
name|idL
parameter_list|,
name|RecordId
name|idR
parameter_list|,
name|String
name|filter
parameter_list|,
name|PrintWriter
name|pw
parameter_list|)
throws|throws
name|IOException
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"rev "
operator|+
name|idL
operator|+
literal|".."
operator|+
name|idR
argument_list|)
expr_stmt|;
try|try
block|{
name|NodeState
name|before
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|idL
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
name|NodeState
name|after
init|=
operator|new
name|SegmentNodeState
argument_list|(
name|idR
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|filter
argument_list|)
control|)
block|{
name|before
operator|=
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|after
operator|=
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PrintingDiff
argument_list|(
name|pw
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"#SNFE "
operator|+
name|ex
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|PrintingDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|PrintWriter
name|pw
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|skipProps
decl_stmt|;
specifier|public
name|PrintingDiff
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|pw
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PrintingDiff
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|skipProps
parameter_list|)
block|{
name|this
operator|.
name|pw
operator|=
name|pw
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|skipProps
operator|=
name|skipProps
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|skipProps
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"    + "
operator|+
name|toString
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|skipProps
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"    ^ "
operator|+
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"      - "
operator|+
name|toString
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"      + "
operator|+
name|toString
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|skipProps
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"    - "
operator|+
name|toString
argument_list|(
name|before
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"+ "
operator|+
name|p
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|PrintingDiff
argument_list|(
name|pw
argument_list|,
name|p
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|String
name|p
init|=
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"^ "
operator|+
name|p
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PrintingDiff
argument_list|(
name|pw
argument_list|,
name|p
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"- "
operator|+
name|p
argument_list|)
expr_stmt|;
return|return
name|MISSING_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PrintingDiff
argument_list|(
name|pw
argument_list|,
name|p
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|PropertyState
name|ps
parameter_list|)
block|{
name|StringBuilder
name|val
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|==
name|BINARY
condition|)
block|{
name|String
name|v
init|=
operator|new
name|BlobLengthF
argument_list|()
operator|.
name|apply
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
argument_list|)
decl_stmt|;
name|val
operator|.
name|append
argument_list|(
literal|" = {"
operator|+
name|v
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|==
name|BINARIES
condition|)
block|{
name|String
name|v
init|=
name|transform
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
argument_list|,
operator|new
name|BlobLengthF
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|val
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|ps
operator|.
name|count
argument_list|()
operator|+
literal|"] = "
operator|+
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ps
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|val
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|ps
operator|.
name|count
argument_list|()
operator|+
literal|"] = "
argument_list|)
expr_stmt|;
name|val
operator|.
name|append
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|val
operator|.
name|append
argument_list|(
literal|" = "
operator|+
name|ps
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ps
operator|.
name|getName
argument_list|()
operator|+
literal|"<"
operator|+
name|ps
operator|.
name|getType
argument_list|()
operator|+
literal|">"
operator|+
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|BlobLengthF
implements|implements
name|Function
argument_list|<
name|Blob
argument_list|,
name|String
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Blob
name|b
parameter_list|)
block|{
return|return
name|safeGetLength
argument_list|(
name|b
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|safeGetLength
parameter_list|(
name|Blob
name|b
parameter_list|)
block|{
try|try
block|{
return|return
name|byteCountToDisplaySize
argument_list|(
name|b
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// missing BlobStore probably
block|}
return|return
literal|"[N/A]"
return|;
block|}
block|}
block|}
end_class

end_unit

