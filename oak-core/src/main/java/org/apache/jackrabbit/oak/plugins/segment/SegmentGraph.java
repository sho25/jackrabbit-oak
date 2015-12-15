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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|SegmentId
operator|.
name|isDataSegmentId
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
name|OutputStream
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
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
name|Functions
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
name|json
operator|.
name|JsonObject
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
name|json
operator|.
name|JsopTokenizer
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

begin_comment
comment|/**  * Utility graph for parsing a segment graph.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SegmentGraph
block|{
specifier|private
name|SegmentGraph
parameter_list|()
block|{ }
comment|/**      * Visitor for receiving call backs while traversing the      * segment graph.      */
specifier|public
interface|interface
name|SegmentGraphVisitor
block|{
comment|/**          * A call to this method indicates that the {@code from} segment          * references the {@code to} segment. Or if {@code to} is {@code null}          * that the {@code from} has no references.          *          * @param from          * @param to          */
name|void
name|accept
parameter_list|(
annotation|@
name|Nonnull
name|UUID
name|from
parameter_list|,
annotation|@
name|CheckForNull
name|UUID
name|to
parameter_list|)
function_decl|;
block|}
comment|/**      * A simple graph representation for a graph with node of type {@code T}.      */
specifier|public
specifier|static
class|class
name|Graph
parameter_list|<
name|T
parameter_list|>
block|{
comment|/** The vertices of this graph */
specifier|public
specifier|final
name|Set
argument_list|<
name|T
argument_list|>
name|vertices
init|=
name|newHashSet
argument_list|()
decl_stmt|;
comment|/** The edges of this graph */
specifier|public
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|Set
argument_list|<
name|T
argument_list|>
argument_list|>
name|edges
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|void
name|addVertex
parameter_list|(
name|T
name|vertex
parameter_list|)
block|{
name|vertices
operator|.
name|add
argument_list|(
name|vertex
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addEdge
parameter_list|(
name|T
name|from
parameter_list|,
name|T
name|to
parameter_list|)
block|{
name|Set
argument_list|<
name|T
argument_list|>
name|tos
init|=
name|edges
operator|.
name|get
argument_list|(
name|from
argument_list|)
decl_stmt|;
if|if
condition|(
name|tos
operator|==
literal|null
condition|)
block|{
name|tos
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
name|edges
operator|.
name|put
argument_list|(
name|from
argument_list|,
name|tos
argument_list|)
expr_stmt|;
block|}
name|tos
operator|.
name|add
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write the segment graph of a file store to a stream.      *<p>      * The graph is written in      *<a href="https://gephi.github.io/users/supported-graph-formats/gdf-format/">the Guess GDF format</a>,      * which is easily imported into<a href="https://gephi.github.io/">Gephi</a>.      * As GDF only supports integers but the segment time stamps are encoded as long      * the {@code epoch} argument is used as a negative offset translating all timestamps      * into a valid int range.      *      * @param fileStore     file store to graph      * @param out           stream to write the graph to      * @param epoch         epoch (in milliseconds)      * @throws Exception      */
specifier|public
specifier|static
name|void
name|writeSegmentGraph
parameter_list|(
annotation|@
name|Nonnull
name|ReadOnlyStore
name|fileStore
parameter_list|,
annotation|@
name|Nonnull
name|OutputStream
name|out
parameter_list|,
annotation|@
name|Nonnull
name|Date
name|epoch
parameter_list|)
throws|throws
name|Exception
block|{
name|checkNotNull
argument_list|(
name|epoch
argument_list|)
expr_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|checkNotNull
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|SegmentNodeState
name|root
init|=
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|Graph
argument_list|<
name|UUID
argument_list|>
name|segmentGraph
init|=
name|parseSegmentGraph
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
name|Graph
argument_list|<
name|UUID
argument_list|>
name|headGraph
init|=
name|parseHeadGraph
argument_list|(
name|root
operator|.
name|getRecordId
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"nodedef>name VARCHAR, label VARCHAR, type VARCHAR, wid VARCHAR, gc INT, t INT, head BOOLEAN\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|UUID
name|segment
range|:
name|segmentGraph
operator|.
name|vertices
control|)
block|{
name|writeNode
argument_list|(
name|segment
argument_list|,
name|writer
argument_list|,
name|headGraph
operator|.
name|vertices
operator|.
name|contains
argument_list|(
name|segment
argument_list|)
argument_list|,
name|epoch
argument_list|,
name|fileStore
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"edgedef>node1 VARCHAR, node2 VARCHAR, head BOOLEAN\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|edge
range|:
name|segmentGraph
operator|.
name|edges
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|UUID
name|from
init|=
name|edge
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|UUID
name|to
range|:
name|edge
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|from
operator|.
name|equals
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|UUID
argument_list|>
name|he
init|=
name|headGraph
operator|.
name|edges
operator|.
name|get
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|boolean
name|inHead
init|=
name|he
operator|!=
literal|null
operator|&&
name|he
operator|.
name|contains
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|from
operator|+
literal|","
operator|+
name|to
operator|+
literal|","
operator|+
name|inHead
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Parse the segment graph of a file store.      *      * @param fileStore     file store to parse      * @return the segment graph rooted as the segment containing the head node      *         state of {@code fileStore}.      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Graph
argument_list|<
name|UUID
argument_list|>
name|parseSegmentGraph
parameter_list|(
annotation|@
name|Nonnull
name|ReadOnlyStore
name|fileStore
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentNodeState
name|root
init|=
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|UUID
argument_list|>
name|roots
init|=
name|newHashSet
argument_list|(
name|root
operator|.
name|getRecordId
argument_list|()
operator|.
name|asUUID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|parseSegmentGraph
argument_list|(
name|fileStore
argument_list|,
name|roots
argument_list|,
name|Functions
operator|.
expr|<
name|UUID
operator|>
name|identity
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Write the gc generation graph of a file store to a stream.      *<p>      * The graph is written in      *<a href="https://gephi.github.io/users/supported-graph-formats/gdf-format/">the Guess GDF format</a>,      * which is easily imported into<a href="https://gephi.github.io/">Gephi</a>.      *      * @param fileStore     file store to graph      * @param out           stream to write the graph to      * @throws Exception      */
specifier|public
specifier|static
name|void
name|writeGCGraph
parameter_list|(
annotation|@
name|Nonnull
name|ReadOnlyStore
name|fileStore
parameter_list|,
annotation|@
name|Nonnull
name|OutputStream
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|checkNotNull
argument_list|(
name|out
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Graph
argument_list|<
name|String
argument_list|>
name|gcGraph
init|=
name|parseGCGraph
argument_list|(
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"nodedef>name VARCHAR\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|gen
range|:
name|gcGraph
operator|.
name|vertices
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|gen
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"edgedef>node1 VARCHAR, node2 VARCHAR\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|edge
range|:
name|gcGraph
operator|.
name|edges
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|from
init|=
name|edge
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|to
range|:
name|edge
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|from
operator|.
name|equals
argument_list|(
name|to
argument_list|)
operator|&&
operator|!
name|to
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|from
operator|+
literal|","
operator|+
name|to
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Parse the gc generation graph of a file store.      *      * @param fileStore     file store to parse      * @return the gc generation graph rooted ad the segment containing the head node      *         state of {@code fileStore}.      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Graph
argument_list|<
name|String
argument_list|>
name|parseGCGraph
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|ReadOnlyStore
name|fileStore
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentNodeState
name|root
init|=
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|UUID
argument_list|>
name|roots
init|=
name|newHashSet
argument_list|(
name|root
operator|.
name|getRecordId
argument_list|()
operator|.
name|asUUID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|parseSegmentGraph
argument_list|(
name|fileStore
argument_list|,
name|roots
argument_list|,
operator|new
name|Function
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
name|UUID
name|segmentId
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
init|=
name|getSegmentInfo
argument_list|(
name|segmentId
argument_list|,
name|fileStore
operator|.
name|getTracker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|get
argument_list|(
literal|"gc"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Parse the segment graph of a file store starting with a given set of root segments.      * The full segment graph is mapped through the passed {@code homomorphism} to the      * graph returned by this function.      *      * @param fileStore     file store to parse      * @param roots         the initial set of segments      * @param homomorphism  map from the segment graph into the returned graph      * @return   the segment graph of {@code fileStore} rooted at {@code roots} and mapped      *           by {@code homomorphism}      * @throws IOException      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Graph
argument_list|<
name|T
argument_list|>
name|parseSegmentGraph
parameter_list|(
annotation|@
name|Nonnull
name|ReadOnlyStore
name|fileStore
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|UUID
argument_list|>
name|roots
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Function
argument_list|<
name|UUID
argument_list|,
name|T
argument_list|>
name|homomorphism
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Graph
argument_list|<
name|T
argument_list|>
name|graph
init|=
operator|new
name|Graph
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|checkNotNull
argument_list|(
name|homomorphism
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|fileStore
argument_list|)
operator|.
name|traverseSegmentGraph
argument_list|(
name|checkNotNull
argument_list|(
name|roots
argument_list|)
argument_list|,
operator|new
name|SegmentGraphVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
annotation|@
name|Nonnull
name|UUID
name|from
parameter_list|,
annotation|@
name|CheckForNull
name|UUID
name|to
parameter_list|)
block|{
name|graph
operator|.
name|addVertex
argument_list|(
name|homomorphism
operator|.
name|apply
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|to
operator|!=
literal|null
condition|)
block|{
name|graph
operator|.
name|addVertex
argument_list|(
operator|(
name|homomorphism
operator|.
name|apply
argument_list|(
name|to
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addEdge
argument_list|(
name|homomorphism
operator|.
name|apply
argument_list|(
name|from
argument_list|)
argument_list|,
name|homomorphism
operator|.
name|apply
argument_list|(
name|to
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|graph
return|;
block|}
comment|/**      * Parser the head graph. The head graph is the sub graph of the segment      * graph containing the {@code root}.      * @param root      * @return  the head graph of {@code root}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|Graph
argument_list|<
name|UUID
argument_list|>
name|parseHeadGraph
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|root
parameter_list|)
block|{
specifier|final
name|Graph
argument_list|<
name|UUID
argument_list|>
name|graph
init|=
operator|new
name|Graph
argument_list|<
name|UUID
argument_list|>
argument_list|()
decl_stmt|;
operator|new
name|SegmentParser
argument_list|()
block|{
specifier|private
name|void
name|addEdge
parameter_list|(
name|RecordId
name|from
parameter_list|,
name|RecordId
name|to
parameter_list|)
block|{
name|graph
operator|.
name|addVertex
argument_list|(
name|from
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addVertex
argument_list|(
name|to
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addEdge
argument_list|(
name|from
operator|.
name|asUUID
argument_list|()
argument_list|,
name|to
operator|.
name|asUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onNode
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|nodeId
parameter_list|)
block|{
name|super
operator|.
name|onNode
argument_list|(
name|parentId
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onTemplate
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|templateId
parameter_list|)
block|{
name|super
operator|.
name|onTemplate
argument_list|(
name|parentId
argument_list|,
name|templateId
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|templateId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMap
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
name|super
operator|.
name|onMap
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMapDiff
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
name|super
operator|.
name|onMapDiff
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMapLeaf
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
name|super
operator|.
name|onMapLeaf
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onMapBranch
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
name|super
operator|.
name|onMapBranch
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|mapId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onProperty
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|propertyId
parameter_list|,
name|PropertyTemplate
name|template
parameter_list|)
block|{
name|super
operator|.
name|onProperty
argument_list|(
name|parentId
argument_list|,
name|propertyId
argument_list|,
name|template
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|propertyId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onValue
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|valueId
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
name|super
operator|.
name|onValue
argument_list|(
name|parentId
argument_list|,
name|valueId
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|valueId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onBlob
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|blobId
parameter_list|)
block|{
name|super
operator|.
name|onBlob
argument_list|(
name|parentId
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onString
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|stringId
parameter_list|)
block|{
name|super
operator|.
name|onString
argument_list|(
name|parentId
argument_list|,
name|stringId
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|stringId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onList
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|listId
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|super
operator|.
name|onList
argument_list|(
name|parentId
argument_list|,
name|listId
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|listId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onListBucket
parameter_list|(
name|RecordId
name|parentId
parameter_list|,
name|RecordId
name|listId
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|capacity
parameter_list|)
block|{
name|super
operator|.
name|onListBucket
argument_list|(
name|parentId
argument_list|,
name|listId
argument_list|,
name|index
argument_list|,
name|count
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
name|addEdge
argument_list|(
name|parentId
argument_list|,
name|listId
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|parseNode
argument_list|(
name|checkNotNull
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|graph
return|;
block|}
specifier|private
specifier|static
name|void
name|writeNode
parameter_list|(
name|UUID
name|node
parameter_list|,
name|PrintWriter
name|writer
parameter_list|,
name|boolean
name|inHead
parameter_list|,
name|Date
name|epoch
parameter_list|,
name|SegmentTracker
name|tracker
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sInfo
init|=
name|getSegmentInfo
argument_list|(
name|node
argument_list|,
name|tracker
argument_list|)
decl_stmt|;
if|if
condition|(
name|sInfo
operator|==
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|node
operator|+
literal|",b,bulk,b,-1,-1,"
operator|+
name|inHead
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|t
init|=
name|asLong
argument_list|(
name|sInfo
operator|.
name|get
argument_list|(
literal|"t"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|ts
init|=
name|t
operator|-
name|epoch
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|ts
operator|>=
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|ts
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"Time stamp ("
operator|+
operator|new
name|Date
argument_list|(
name|t
argument_list|)
operator|+
literal|") not in epoch ("
operator|+
operator|new
name|Date
argument_list|(
name|epoch
operator|.
name|getTime
argument_list|()
operator|+
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
operator|+
literal|" - "
operator|+
operator|new
name|Date
argument_list|(
name|epoch
operator|.
name|getTime
argument_list|()
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|node
operator|+
literal|","
operator|+
name|sInfo
operator|.
name|get
argument_list|(
literal|"sno"
argument_list|)
operator|+
literal|",data"
operator|+
literal|","
operator|+
name|sInfo
operator|.
name|get
argument_list|(
literal|"wid"
argument_list|)
operator|+
literal|","
operator|+
name|sInfo
operator|.
name|get
argument_list|(
literal|"gc"
argument_list|)
operator|+
literal|","
operator|+
name|ts
operator|+
literal|","
operator|+
name|inHead
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|long
name|asLong
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|string
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSegmentInfo
parameter_list|(
name|UUID
name|node
parameter_list|,
name|SegmentTracker
name|tracker
parameter_list|)
block|{
if|if
condition|(
name|isDataSegmentId
argument_list|(
name|node
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
condition|)
block|{
name|SegmentId
name|id
init|=
name|tracker
operator|.
name|getSegmentId
argument_list|(
name|node
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|node
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|info
init|=
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|getSegmentInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|JsopTokenizer
name|tokenizer
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
return|return
name|JsonObject
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
operator|.
name|getProperties
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

