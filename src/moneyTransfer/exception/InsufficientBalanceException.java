package moneyTransfer.exception;

/**
 * 余额不足异常
 * 当账户余额不足以支付指定金额时抛出
 */
public class InsufficientBalanceException extends Exception {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
