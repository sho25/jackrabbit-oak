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
name|remote
package|;
end_package

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
name|List
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

begin_comment
comment|/**  * Collection of operations available on a remote repository once the user  * correctly logged in.  *<p/>  * Operations working on pure content, like reading a tree or committing  * changes, requires a revision. A revision represents a snapshot of the  * repository in a specified point in time. This concept enables repeatable  * reads and consistent writes.  *<p/>  * When binary data is involved, this interface exposes methods to read and  * write arbitrary binary data from and to the repository. A binary data is  * considered an immutable collection of bytes that can be referenced by an  * identifier.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RemoteSession
block|{
comment|/**      * Read the latest revision in the repository.      *<p/>      * This operation is always meant to succeed, because the repository will      * always have an initial revision to return to the caller.      *      * @return The latest revision in the repository.      */
name|RemoteRevision
name|readLastRevision
parameter_list|()
function_decl|;
comment|/**      * Read a revision given a string representation of the revision itself.      *<p/>      * This operation may fail for a number of reasons. In example, the string      * passed to this method is not a valid revision, or this string represents      * a revision that was valid in the past but it is no more valid.      *      * @param revision The string representation of the revision.      * @return The revision represented by the string passed to this method, or      * {@code null} if the string representation is invalid.      */
name|RemoteRevision
name|readRevision
parameter_list|(
name|String
name|revision
parameter_list|)
function_decl|;
comment|/**      * Read a sub-tree from the repository at the given revision. Some filters      * may be applied to the tree to avoid reading unwanted information.      *      * @param revision The revision representing the state of the repository to      *                 read from.      * @param path     The path of the root of the subtree to read.      * @param filters  Filters to apply to the returned tree.      * @return The tree requested by the given path, filtered according to the      * provided filters. The method can return {@code null} if the root of the      * tree is not found in the repository for the given revision.      */
name|RemoteTree
name|readTree
parameter_list|(
name|RemoteRevision
name|revision
parameter_list|,
name|String
name|path
parameter_list|,
name|RemoteTreeFilters
name|filters
parameter_list|)
function_decl|;
comment|/**      * Create an operation to represent the addition of a new node in the      * repository.      *      * @param path       Path of the new node to create.      * @param properties Initial set of properties attached to the new node.      * @return An operation representing the addition of a new node.      */
name|RemoteOperation
name|createAddOperation
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteValue
argument_list|>
name|properties
parameter_list|)
function_decl|;
comment|/**      * Create an operation representing the removal of an existing node from the      * repository.      *      * @param path Path of the node to remove.      * @return An operation representing the removal of an existing node.      */
name|RemoteOperation
name|createRemoveOperation
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Create an operation representing the creation or modification of a      * property of an existing node.      *      * @param path  Path of the node where the property is or will be attached      *              to.      * @param name  Name of the property to set.      * @param value Value of the property.      * @return An operation representing the creation or modification of a      * property of an existing node.      */
name|RemoteOperation
name|createSetOperation
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|RemoteValue
name|value
parameter_list|)
function_decl|;
comment|/**      * Create an operation to represent the removal of an existing property from      * an existing node in the repository.      *      * @param path Path of the node where the property is attached to.      * @param name Name of the property to remove.      * @return An operation representing the removal of a property.      */
name|RemoteOperation
name|createUnsetOperation
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Create an operation to represent the copy of a subtree into another      * location into the repository.      *      * @param source Path of the root of the subtree to copy.      * @param target Path where the subtree should be copied to.      * @return An operation representing a copy of a subtree.      */
name|RemoteOperation
name|createCopyOperation
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
function_decl|;
comment|/**      * Create an operation to represent the move of a subtree into another      * location into the repository.      *      * @param source Path of the root of the source subtree to move.      * @param target Path where the subtree should be moved to.      * @return An operation representing a move of a subtree.      */
name|RemoteOperation
name|createMoveOperation
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
function_decl|;
comment|/**      * Create an operation that represents the aggregation of multiple, simpler      * operations. The aggregated operations are applied in the same sequence      * provided by this method.      *      * @param operations Sequence of operations to aggregate.      * @return An operation that, when executed, will execute the provided      * operations in the provided sequence.      */
name|RemoteOperation
name|createAggregateOperation
parameter_list|(
name|List
argument_list|<
name|RemoteOperation
argument_list|>
name|operations
parameter_list|)
function_decl|;
comment|/**      * Commit some changes to the repository. The changes are represented by an      * operation. The operation will be applied on the repository state      * represented by the given revision.      *      * @param revision  Revision where the changes should be applied to.      * @param operation Operation to change the state of the repository.      * @return A new revision representing the new state of the repository where      * the changes are applied.      * @throws RemoteCommitException if the provided operation can't be      *                               performed on the provided repository      *                               state.      */
name|RemoteRevision
name|commit
parameter_list|(
name|RemoteRevision
name|revision
parameter_list|,
name|RemoteOperation
name|operation
parameter_list|)
throws|throws
name|RemoteCommitException
function_decl|;
comment|/**      * Read a binary ID given a string representation of the binary ID itself.      *<p/>      * This operations may fail for a number of reasons. In example, the string      * doesn't represent a valid binary ID, or the string represents a binary ID      * that was valid in the past but is no more valid.      *      * @param binaryId String representation of the binary ID.      * @return The binary ID read from the repository. This method may return      * {@code null} if the string representation of the binary ID is not valid.      */
name|RemoteBinaryId
name|readBinaryId
parameter_list|(
name|String
name|binaryId
parameter_list|)
function_decl|;
comment|/**      * Read a binary object from the repository according to the given filters.      *<p/>      * In the case of a binary object, filters are really simple. At most, it is      * possible to read just a portion of the binary object instead of reading      * it in its entirety.      *      * @param binaryId Binary ID referring to the binary object to read.      * @param filters  Filters to apply to the returned binary object.      * @return A stream representing the filtered content of the binary object.      */
name|InputStream
name|readBinary
parameter_list|(
name|RemoteBinaryId
name|binaryId
parameter_list|,
name|RemoteBinaryFilters
name|filters
parameter_list|)
function_decl|;
comment|/**      * Read the length of a binary object from the repository.      *      * @param binaryId Binary ID referring to the binary object whose length      *                 should be read.      * @return The length of the binary object.      */
name|long
name|readBinaryLength
parameter_list|(
name|RemoteBinaryId
name|binaryId
parameter_list|)
function_decl|;
comment|/**      * Write a binary object into the repository and return a binary ID      * referencing to it.      *      * @param stream Stream representing the binary object to write.      * @return Binary ID referencing the binary object written in the      * repository.      */
name|RemoteBinaryId
name|writeBinary
parameter_list|(
name|InputStream
name|stream
parameter_list|)
function_decl|;
comment|/**      * Performs a search in the content and returns a set of search results.      *      * @param revision The revision that should be used when searching the      *                 content.      * @param query    The query. It may contain placeholders that are to be      *                 substituted with the actual parameters.      * @param language The language the query is written in. It identifies the      *                 syntax of the query.      * @param offset   How many rows to skip when returning the results.      * @param limit    How many results to return.      * @return Search results.      * @throws RemoteQueryParseException if the query can't be correctly      *                                   parsed.      */
name|RemoteResults
name|search
parameter_list|(
name|RemoteRevision
name|revision
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|language
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|limit
parameter_list|)
throws|throws
name|RemoteQueryParseException
function_decl|;
block|}
end_interface

end_unit

