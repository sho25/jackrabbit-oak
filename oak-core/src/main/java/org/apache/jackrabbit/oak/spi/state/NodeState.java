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
name|spi
operator|.
name|state
package|;
end_package

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

begin_comment
comment|/**  * A node in a content tree consists of child nodes and properties, each  * of which evolves through different states during its lifecycle. This  * interface represents a specific, immutable state of a node. The state  * consists of an unordered set of name -&gt; item mappings, where  * each item is either a property or a child node.  *<p>  * Depending on context, a NodeState instance can be interpreted as  * representing the state of just that node, of the subtree starting at  * that node, or of an entire tree in case it's a root node.  *<p>  * The crucial difference between this interface and the similarly named  * class in Jackrabbit 2.x is that this interface represents a specific,  * immutable state of a node, whereas the Jackrabbit 2.x class represented  * the<em>current</em> state of a node.  *  *<h2>Immutability and thread-safety</h2>  *<p>  * As mentioned above, all node and property states are always immutable.  * Thus repeating a method call is always guaranteed to produce the same  * result as before unless some internal error occurs (see below). This  * immutability only applies to a specific state instance. Different states  * of a node can obviously be different, and in some cases even different  * instances of the same state may behave slightly differently. For example  * due to performance optimization or other similar changes the iteration  * order of properties or child nodes may be different for two instances  * of the same state.  *<p>  * In addition to being immutable, a specific state instance guaranteed to  * be fully thread-safe. Possible caching or other internal changes need to  * be properly synchronized so that any number of concurrent clients can  * safely access a state instance.  *  *<h2>Persistence and error-handling</h2>  *<p>  * A node state can be (and often is) backed by local files or network  * resources. All IO operations or related concerns like caching should be  * handled transparently below this interface. Potential IO problems and  * recovery attempts like retrying a timed-out network access need to be  * handled below this interface, and only hard errors should be thrown up  * as {@link RuntimeException unchecked exceptions} that higher level code  * is not expected to be able to recover from.  *<p>  * Since this interface exposes no higher level constructs like locking,  * node types or even path parsing, there's no way for content access to  * fail because of such concerns. Such functionality and related checked  * exceptions or other control flow constructs should be implemented on  * a higher level above this interface. On the other hand read access  * controls<em>can</em> be implemented below this interface, in which  * case some content that would otherwise be accessible might not show  * up through such an implementation.  *  *<h2>Existence and iterability of node states</h2>  *<p>  * The {@link #getChildNode(String)} method is special in that it  *<em>never</em> returns a {@code null} value, even if the named child  * node does not exist. Instead a client should use the {@link #exists()}  * method on the returned child state to check whether that node exists.  * The purpose of this separation of concerns is to allow an implementation  * to lazily load content only when it's actually read instead of just  * traversed. It also simplifies client code by avoiding the need for many  * {@code null} checks when traversing paths.  *<p>  * The<em>iterability</em> of a node is a related concept to the  * above-mentioned existence. A node state is<em>iterable</em> if it  * is included in the return values of the {@link #getChildNodeCount()},  * {@link #getChildNodeNames()} and {@link #getChildNodeEntries()} methods.  * An iterable node is guaranteed to exist, though not all existing nodes  * are necessarily iterable.  *<p>  * Furthermore, a non-existing node is guaranteed to contain no properties  * or iterable child nodes. It can, however contain non-iterable children.  * Such scenarios are typically the result of access control restrictions.  *  *<h2>Decoration and virtual content</h2>  *<p>  * Not all content exposed by this interface needs to be backed by actual  * persisted data. An implementation may want to provide derived data,  * like for example the aggregate size of the entire subtree as an  * extra virtual property. A virtualization, sharding or caching layer  * could provide a composite view over multiple underlying trees.  * Or a an access control layer could decide to hide certain content  * based on specific rules. All such features need to be implemented  * according to the API contract of this interface. A separate higher level  * interface needs to be used if an implementation can't for example  * guarantee immutability of exposed content as discussed above.  *  *<h2>Equality and hash codes</h2>  *<p>  * Two node states are considered equal if and only if their existence,  * properties and iterable child nodes match, regardless of ordering. The  * {@link Object#equals(Object)} method needs to be implemented so that it  * complies with this definition. And while node states are not meant for  * use as hash keys, the {@link Object#hashCode()} method should still be  * implemented according to this equality contract.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeState
block|{
comment|/**      * Checks whether this node exists. See the above discussion about      * the existence of node states.      *      * @return {@code true} if this node exists, {@code false} if not      */
name|boolean
name|exists
parameter_list|()
function_decl|;
comment|/**      * Checks whether the named property exists. The implementation is      * equivalent to {@code getProperty(name) != null}, but may be optimized      * to avoid having to load the property value.      *      * @param name property name      * @return {@code true} if the named property exists,      *         {@code false} otherwise      */
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the named property. The name is an opaque string and      * is not parsed or otherwise interpreted by this method.      *<p>      * The namespace of properties and child nodes is shared, so if      * this method returns a non-{@code null} value for a given      * name, then {@link #getChildNode(String)} is guaranteed to return      * a<em>non-existing</em> {@link NodeState} for the same name.      *      * @param name name of the property to return      * @return named property, or {@code null} if not found      */
annotation|@
name|CheckForNull
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the boolean value of the named property. The implementation      * is equivalent to the following code, but may be optimized.      *<pre>      * PropertyState property = state.getProperty(name);      * return property != null      *&& property.getType() == Type.BOOLEAN      *&& property.getValue(Type.BOOLEAN);      *</pre>      *      * @param name property name      * @return boolean value of the named property, or {@code false}      */
name|boolean
name|getBoolean
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the long value of the named property. The implementation      * is equivalent to the following code, but may be optimized.      *<pre>      * PropertyState property = state.getProperty(name);      * if (property != null&& property.getType() == Type.LONG) {      *     return property.getValue(Type.LONG);      * } else {      *     return 0;      * }      *</pre>      *      * @param name property name      * @return long value of the named property, or zero      */
name|long
name|getLong
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the string value of the named property. The implementation      * is equivalent to the following code, but may be optimized.      *<pre>      * PropertyState property = state.getProperty(name);      * if (property != null&& property.getType() == Type.STRING) {      *     return property.getValue(Type.STRING);      * } else {      *     return null;      * }      *</pre>      *      * @param name property name      * @return string value of the named property, or {@code null}      */
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the name value of the named property. The implementation      * is equivalent to the following code, but may be optimized.      *<pre>      * PropertyState property = state.getProperty(name);      * if (property != null&& property.getType() == Type.NAME) {      *     return property.getValue(Type.NAME);      * } else {      *     return null;      * }      *</pre>      *      * @param name property name      * @return name value of the named property, or {@code null}      */
annotation|@
name|CheckForNull
name|String
name|getName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the name values of the named property. The implementation      * is equivalent to the following code, but may be optimized.      *<pre>      * PropertyState property = state.getProperty(name);      * if (property != null&& property.getType() == Type.NAMES) {      *     return property.getValue(Type.NAMES);      * } else {      *     return Collections.emptyList();      * }      *</pre>      *      * @param name property name      * @return name values of the named property, or an empty collection      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the number of properties of this node.      *      * @return number of properties      */
name|long
name|getPropertyCount
parameter_list|()
function_decl|;
comment|/**      * Returns an iterable of the properties of this node. Multiple      * iterations are guaranteed to return the properties in the same      * order, but the specific order used is implementation-dependent      * and may change across different states of the same node.      *      * @return properties in some stable order      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Checks whether the named child node exists. The implementation      * is equivalent to {@code getChildNode(name).exists()}.      *      * @param name name of the child node      * @return {@code true} if the named child node exists,      *         {@code false} otherwise      */
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the named, possibly non-existent, child node. The name is an      * opaque string and is not parsed or otherwise interpreted by this method.      * Use the {@link #exists()} method on the returned child node to      * determine whether the node exists or not.      *<p>      * The namespace of properties and child nodes is shared, so if      * this method returns an<em>existing</em> {@link NodeState} for      * a given name, then {@link #getProperty(String)} is guaranteed      * to return {@code null} for the same name.      *      * @param name name of the child node to return      * @return named child node      */
annotation|@
name|Nonnull
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the number of<em>iterable</em> child nodes of this node.      *      * @return number of iterable child nodes      */
name|long
name|getChildNodeCount
parameter_list|()
function_decl|;
comment|/**      * Returns the names of all<em>iterable</em> child nodes.      *      * @return child node names in some stable order      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
function_decl|;
comment|/**      * Returns the<em>iterable</em> child node entries of this instance.      * Multiple iterations are guaranteed to return the child nodes in      * the same order, but the specific order used is implementation      * dependent and may change across different states of the same node.      *<p>      *<i>Note on cost and performance:</i> while it is possible to iterate over      * all child {@code NodeState}s with the two methods {@link      * #getChildNodeNames()} and {@link #getChildNode(String)}, this method is      * considered more efficient because an implementation can potentially      * perform the retrieval of the name and {@code NodeState} in one call.      * This results in O(n) vs. O(n log n) when iterating over the child node      * names and then look up the {@code NodeState} by name.      *      * @return child node entries in some stable order      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
function_decl|;
comment|/**      * Returns a builder for constructing a new node state based on      * this state, i.e. starting with all the properties and child nodes      * of this state.      *      * @since Oak 0.6      * @return node builder based on this state      */
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|()
function_decl|;
comment|/**      * Compares this node state against the given base state. Any differences      * are reported by calling the relevant added/changed/deleted methods of      * the given handler.      *<p>      * TODO: Define the behavior of this method with regards to      * iterability/existence of child nodes.      *      * @param base base state      * @param diff handler of node state differences      * @since 0ak 0.4, return value added in 0.7      */
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
function_decl|;
comment|/**      * Predicate that checks the existence of NodeState instances.      */
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|EXISTS
init|=
operator|new
name|Predicate
argument_list|<
name|NodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|NodeState
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
operator|&&
name|input
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

