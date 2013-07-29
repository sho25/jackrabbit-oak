begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
package|;
end_package

begin_comment
comment|/**  * An instances of this class represents a lazy value of type {@code T}.  * {@code LazyValue} implements an evaluate by need semantics:  * {@link #createValue()} is called exactly once when {@link #get()}  * is called for the first time.  *<p>  * {@code LazyValue} instances are thread safe.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LazyValue
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|volatile
name|T
name|value
decl_stmt|;
comment|/**      * Factory method called to create the value on an as need basis.      * @return a new instance for {@code T}.      */
specifier|protected
specifier|abstract
name|T
name|createValue
parameter_list|()
function_decl|;
comment|/**      * Get value. Calls {@link #createValue()} if called for the first time.      * @return  the value      */
specifier|public
name|T
name|get
parameter_list|()
block|{
comment|// Double checked locking is fine since Java 5 as long as value is volatile.
comment|// See http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|createValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

