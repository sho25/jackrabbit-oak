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
name|jcr
operator|.
name|tck
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|test
operator|.
name|ConcurrentTestSuite
import|;
end_import

begin_class
specifier|public
class|class
name|ObservationIT
extends|extends
name|ConcurrentTestSuite
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|ObservationIT
argument_list|()
return|;
block|}
specifier|public
name|ObservationIT
parameter_list|()
block|{
name|super
argument_list|(
literal|"JCR observation tests"
argument_list|)
expr_stmt|;
name|addTest
argument_list|(
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|test
operator|.
name|api
operator|.
name|observation
operator|.
name|TestAll
operator|.
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

