package moneyTransfer.exception;

/**
 * 账户冻结异常
 * 当账户状态为冻结时无法进行操作
 */
public class AccountFrozenException extends Exception {
    
    public AccountFrozenException(String message) {
        super(message);
    }
    
    public AccountFrozenException(String message, Throwable cause) {
        super(message, cause);
    }
}
