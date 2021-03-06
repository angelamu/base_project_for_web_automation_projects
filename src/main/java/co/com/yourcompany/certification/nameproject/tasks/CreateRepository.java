package co.com.yourcompany.certification.nameproject.tasks;

import co.com.yourcompany.certification.nameproject.exceptions.RepositoryAlreadyExistsException;
import co.com.yourcompany.certification.nameproject.exceptions.RepositoryModelCreationException;
import co.com.yourcompany.certification.nameproject.interactions.SelectDropDownButton;
import co.com.yourcompany.certification.nameproject.model.Repository;
import co.com.yourcompany.certification.nameproject.model.enumerables.License;
import co.com.yourcompany.certification.nameproject.util.builder.Builder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.conditions.Check;

import static co.com.yourcompany.certification.nameproject.exceptions.RepositoryAlreadyExistsException.withMessageBy;
import static co.com.yourcompany.certification.nameproject.model.enumerables.GitIgnore.NONE;
import static co.com.yourcompany.certification.nameproject.userinterface.CreateNewRepositoryPage.*;
import static co.com.yourcompany.certification.nameproject.userinterface.GitHubHomePage.NEW_REPOSITORY;
import static co.com.yourcompany.certification.nameproject.util.validations.Validations.isNotEmptyOrNull;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

public class CreateRepository implements Task {

    private final Repository repository;

    public CreateRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(NEW_REPOSITORY),
                Enter.theValue(repository.name()).into(REPOSITORY_NAME)
        );

        actor.should(seeThat(the(MESSAGE_REPOSITORY_ALREADY_EXISTS), isNotVisible())
                .orComplainWith(RepositoryAlreadyExistsException.class,
                        withMessageBy(repository.name())));

        actor.attemptsTo(
                Check.whether(isNotEmptyOrNull(repository.description()))
                        .andIfSo(Enter.theValue(repository.description()).into(REPOSITORY_DESCRIPTION)),
                Check.whether(repository.isInitializeWithREADME())
                        .andIfSo(Click.on(INITIALIZE_THIS_REPOSITORY_WITH_README)),
                Check.whether(repository.gitIgnore() != NONE)
                        .andIfSo(SelectDropDownButton.addGitIgnoreFilteringBy(repository.gitIgnore())),
                Check.whether(repository.license() != License.NONE)
                        .andIfSo(SelectDropDownButton.addLicenseFilteringBy(repository.license())),
                Click.on(CREATE_REPOSITORY)
        );
    }

    public static CreateRepository withTheFollowingData(Builder<Repository> builder) throws RepositoryModelCreationException {
        return instrumented(CreateRepository.class, builder.build());
    }

}
